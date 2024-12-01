/*
 *  Copyright 2024 Emmanouil Gkatziouras
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.gkatzioura.gcp.schema.maven;

import com.google.pubsub.v1.Schema;
import com.google.pubsub.v1.Schema.Type;
import com.google.pubsub.v1.SchemaName;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

class LocalSchemaStorage {

  private final File directory;

  public LocalSchemaStorage(File directory) {
    this.directory = directory;
  }

  File save(Schema schema) throws IOException {
    String path = location(schema);

    try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path))) {
      bufferedWriter.write(schema.getDefinition());
      bufferedWriter.flush();
    }

    return new File(path);
  }

  String location(Schema schema) {
    validateType(schema);

    SchemaName schemaName = SchemaName.parse(removeRevision(schema));
    return new File(directory, schemaName.getProject() + "/" + schemaName.getSchema() + suffixFor(
        schema.getType())).getPath();
  }

  private static String removeRevision(Schema schema) {
    return schema.getName().split("@")[0];
  }

  private static void validateType(Schema schema) {
    if (!schema.getType().equals(Type.AVRO) &&
        !schema.getType().equals(Type.PROTOCOL_BUFFER)) {
      throw new IllegalArgumentException("Only Avro and protobufs are supported");
    }
  }

  private static String suffixFor(Type type) {
    switch (type) {
      case AVRO:
        return ".avsc";
      case PROTOCOL_BUFFER:
        return ".proto";
    }

    throw new IllegalArgumentException("Only avro and proto supported");
  }

  static LocalSchemaStorage create(Log log, File file, String project)
      throws MojoExecutionException {
    try {
      log.debug(
          String.format("Checking if '%s' exists and is not a directory.", file));
      if (file.exists() && !file.isDirectory()) {
        throw new IllegalStateException("outputDirectory must be a directory");
      }
      log.debug(String.format("Checking if outputDirectory('%s') exists.", file));

      File projectSubPath = new File(file.getPath() + "/" + project);
      if (!projectSubPath.isDirectory()) {
        log.debug(String.format("Creating outputDirectory('%s') and project subpath.", file, project));
        if (!projectSubPath.mkdirs()) {
          throw new IllegalStateException(
              "Could not create output directory " + file);
        }
      }

      return new LocalSchemaStorage(file);
    } catch (Exception ex) {
      throw new MojoExecutionException("Exception thrown while creating outputDirectory", ex);
    }
  }

}
