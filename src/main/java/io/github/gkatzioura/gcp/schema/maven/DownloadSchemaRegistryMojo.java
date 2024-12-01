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

import com.google.cloud.pubsub.v1.SchemaServiceClient;
import com.google.pubsub.v1.ProjectName;
import com.google.pubsub.v1.Schema;
import com.google.pubsub.v1.Schema.Type;
import io.grpc.StatusRuntimeException;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "download")
public class DownloadSchemaRegistryMojo extends AbstractMojo implements Closeable {

  private SchemaServiceClient client;

  @Parameter(required = true)
  private String project;

  @Parameter
  private String schemaType;

  @Parameter(required = false)
  private List<String> subjectPatterns = new ArrayList<>();

  @Parameter(required = false)
  private List<String> versions = new ArrayList<>();

  private PatternMatcher patternVersions;

  @Parameter(required = true)
  File outputDirectory;

  @Parameter(property = "gcp-schema-registry.skip")
  boolean skip;

  private PatternFactory patternFactory;

  private SchemaRepository schemaRepository;

  @Override
  public void execute() throws MojoExecutionException {
    if (skip) {
      getLog().info("Plugin execution has been skipped");
      return;
    }

    SchemaRepository repository = schemaRepository();
    LocalSchemaStorage localStorage = LocalSchemaStorage.create(getLog(), outputDirectory, project);

    List<Schema> schemas = listSchemas(repository);

    for (Schema schema : schemas) {
      persistSchema(schema, localStorage);
    }

    try {
      close();
    } catch (IOException e) {
      throw new MojoExecutionException("Exception while closing schema registry client", e);
    }
  }

  private static void persistSchema(Schema schema, LocalSchemaStorage localStorage)
      throws MojoExecutionException {
    try {
      localStorage.save(schema);
    } catch (IOException e) {
      throw new MojoExecutionException("Could not store schema");
    }
  }

  private List<Schema> listSchemas(SchemaRepository repository) throws MojoExecutionException {
    try {
      return repository.list()
          .stream()
          .filter(this::filterSchemaType)
          .map(s -> patternVersions().matches(s))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .map(repository::fetch)
          .collect(Collectors.toList());
    } catch (StatusRuntimeException e) {
      throw new MojoExecutionException("Exception while interacting with PubSub schema registry",
          e);
    }
  }

  private boolean filterSchemaType(Schema s) {
    if (schemaType == null) {
      return true;
    }

    return Type.valueOf(schemaType).equals(s.getType());
  }

  private PatternMatcher patternVersions() {
    if (patternVersions == null) {
      this.patternVersions = patternFactory().create(subjectPatterns, versions);
    }

    return this.patternVersions;
  }

  private SchemaRepository schemaRepository() throws MojoExecutionException {
    if (schemaRepository == null) {
      try {
        this.client = SchemaServiceClient.create();
        this.schemaRepository = new SchemaRepository(getLog(), client, ProjectName.of(project));
      } catch (IOException e) {
        throw new MojoExecutionException("Could not initiate SchemaServiceClient", e);
      }
    }

    return this.schemaRepository;
  }

  private PatternFactory patternFactory() {
    if (patternFactory == null) {
      patternFactory = new PatternFactory(getLog());
    }

    return patternFactory;
  }

  @Override
  public void close() throws IOException {
    if (client != null) {
      client.close();
    }
  }

}
