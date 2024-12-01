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

import static io.github.gkatzioura.gcp.schema.maven.TestUtils.PROJECT_NAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.cloud.pubsub.v1.SchemaServiceClient;
import com.google.cloud.pubsub.v1.SchemaServiceClient.ListSchemasPagedResponse;
import com.google.pubsub.v1.ProjectName;
import com.google.pubsub.v1.Schema;
import com.google.pubsub.v1.SchemaName;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Test;

class SchemaRepositoryTest {

  @Test
  void shouldRetrieveSchemas() {
    Log log = mock(Log.class);
    ProjectName projectName = ProjectName.of(PROJECT_NAME);

    SchemaServiceClient schemaServiceClient = mock(SchemaServiceClient.class);
    ListSchemasPagedResponse listSchemasPagedResponse = mock(ListSchemasPagedResponse.class);
    Schema schema = mock(Schema.class);
    List<Schema> schemas = new ArrayList<>();
    schemas.add(schema);

    when(listSchemasPagedResponse.iterateAll()).thenReturn(schemas);
    when(schemaServiceClient.listSchemas(projectName)).thenReturn(listSchemasPagedResponse);
    SchemaRepository schemaRepository = new SchemaRepository(log, schemaServiceClient, projectName);

    List<Schema> retrievedSchemas = schemaRepository.list();
    assertFalse(retrievedSchemas.isEmpty());
  }

  @Test
  void fetch() {
    Log log = mock(Log.class);
    ProjectName projectName = ProjectName.of(PROJECT_NAME);

    String schema1 = "a23@hq";
    SchemaServiceClient schemaServiceClient = mock(SchemaServiceClient.class);
    Schema schema = mock(Schema.class);
    when(schema.getName()).thenReturn(schema1);

    SchemaName schemaName = SchemaName.newBuilder()
        .setProject(projectName.getProject())
        .setSchema(schema1)
        .build();
    when(schemaServiceClient.getSchema(schemaName)).thenReturn(schema);

    SchemaRepository schemaRepository = new SchemaRepository(log, schemaServiceClient, projectName);
    Schema retrievedSchema = schemaRepository.fetch(schema1);
    assertEquals(schema1, retrievedSchema.getName());
  }

  @Test
  void fetchFullPath() {
    Log log = mock(Log.class);
    ProjectName projectName = ProjectName.of(PROJECT_NAME);

    String schema1 = "projects/test-project/schemas/a23@hq";
    SchemaServiceClient schemaServiceClient = mock(SchemaServiceClient.class);
    Schema schema = mock(Schema.class);
    when(schema.getName()).thenReturn(schema1);

    SchemaName schemaName = SchemaName.newBuilder()
        .setProject(projectName.getProject())
        .setSchema("a23@hq")
        .build();
    when(schemaServiceClient.getSchema(schemaName)).thenReturn(schema);

    SchemaRepository schemaRepository = new SchemaRepository(log, schemaServiceClient, projectName);
    Schema retrievedSchema = schemaRepository.fetch(schema1);
    assertEquals(schema1, retrievedSchema.getName());
  }

}