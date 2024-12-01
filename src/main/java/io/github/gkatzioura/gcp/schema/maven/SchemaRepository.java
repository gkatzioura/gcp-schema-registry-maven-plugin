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
import com.google.cloud.pubsub.v1.SchemaServiceClient.ListSchemasPagedResponse;
import com.google.pubsub.v1.ProjectName;
import com.google.pubsub.v1.Schema;
import com.google.pubsub.v1.SchemaName;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugin.logging.Log;

class SchemaRepository {

  private final Log log;
  private final SchemaServiceClient schemaServiceClient;
  private final ProjectName projectName;

  public SchemaRepository(Log log, SchemaServiceClient schemaServiceClient, ProjectName projectName) {
    this.log = log;
    this.schemaServiceClient = schemaServiceClient;
    this.projectName = projectName;
  }

  List<Schema> list() {
    ListSchemasPagedResponse listSchemasPagedResponse = schemaServiceClient.listSchemas(projectName);
    List<Schema> schemas = new ArrayList<>();
    listSchemasPagedResponse.iterateAll().forEach(schemas::add);
    return schemas;
  }

  Schema fetch(String name) {
    final SchemaName schemaName = extractSchemaName(name);
    return schemaServiceClient.getSchema(schemaName);

  }

  private SchemaName extractSchemaName(String name) {
    if(SchemaName.isParsableFrom(name)) {
      return SchemaName.parse(name);
    } else {
      return SchemaName.newBuilder()
          .setProject(projectName.getProject())
          .setSchema(name)
          .build();
    }
  }

}
