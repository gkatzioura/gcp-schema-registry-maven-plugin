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
import static io.github.gkatzioura.gcp.schema.maven.TestUtils.TEST_AVRO_SCHEMA;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.pubsub.v1.SchemaServiceClient;
import com.google.pubsub.v1.Schema;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class DownloadSchemaRegistryMojoTest {

  @BeforeEach
  void setUp() {
    new File("./test-project").mkdirs();
  }

  @AfterEach
  void tearDown() {
    new File("./test-project").delete();
  }

  @Test
  void shouldDownloadFiles() throws MojoExecutionException, MojoFailureException {
    SchemaRepository schemaRepository = mock(SchemaRepository.class);
    DownloadSchemaRegistryMojo downloadSchemaRegistryMojo = spy(new DownloadSchemaRegistryMojo());
    ReflectionTestUtils.setField(downloadSchemaRegistryMojo,"schemaRepository", schemaRepository);
    ReflectionTestUtils.setField(downloadSchemaRegistryMojo,"project", PROJECT_NAME);
    ReflectionTestUtils.setField(downloadSchemaRegistryMojo, "outputDirectory", new File("./"));

    List<Schema> schemas = new ArrayList<>();
    schemas.add(TEST_AVRO_SCHEMA);
    when(schemaRepository.list()).thenReturn(schemas);
    when(schemaRepository.fetch(anyString())).thenReturn(TEST_AVRO_SCHEMA);

    downloadSchemaRegistryMojo.execute();

    File file = new File("./test-project/an-avro-schema-name.avsc");
    assertTrue(file.exists());
  }

  @Test
  void shouldDownloadOnlyAvroFiles() throws MojoExecutionException {
    SchemaRepository schemaRepository = mock(SchemaRepository.class);
    DownloadSchemaRegistryMojo downloadSchemaRegistryMojo = spy(new DownloadSchemaRegistryMojo());
    ReflectionTestUtils.setField(downloadSchemaRegistryMojo,"schemaRepository", schemaRepository);
    ReflectionTestUtils.setField(downloadSchemaRegistryMojo,"project", PROJECT_NAME);
    ReflectionTestUtils.setField(downloadSchemaRegistryMojo, "outputDirectory", new File("./"));
    ReflectionTestUtils.setField(downloadSchemaRegistryMojo, "schemaType",  "AVRO");

    List<Schema> schemas = new ArrayList<>();
    schemas.add(TEST_AVRO_SCHEMA);
    when(schemaRepository.list()).thenReturn(schemas);
    when(schemaRepository.fetch(anyString())).thenReturn(TEST_AVRO_SCHEMA);

    downloadSchemaRegistryMojo.execute();

    File file = new File("./test-project/an-avro-schema-name.avsc");
    assertTrue(file.exists());
  }

  @Test
  void shouldNotDownloadOtherTypeSpecified() throws MojoExecutionException {
    SchemaRepository schemaRepository = mock(SchemaRepository.class);
    DownloadSchemaRegistryMojo downloadSchemaRegistryMojo = spy(new DownloadSchemaRegistryMojo());
    ReflectionTestUtils.setField(downloadSchemaRegistryMojo,"schemaRepository", schemaRepository);
    ReflectionTestUtils.setField(downloadSchemaRegistryMojo,"project", PROJECT_NAME);
    ReflectionTestUtils.setField(downloadSchemaRegistryMojo, "outputDirectory", new File("./"));
    ReflectionTestUtils.setField(downloadSchemaRegistryMojo, "schemaType",  "PROTOCOL_BUFFER");

    List<Schema> schemas = new ArrayList<>();
    schemas.add(TEST_AVRO_SCHEMA);
    when(schemaRepository.list()).thenReturn(schemas);

    downloadSchemaRegistryMojo.execute();

    verify(schemaRepository,times(0)).fetch(anyString());
  }

  @Test
  void shouldThrowExceptionOnGCPError() {
    SchemaRepository schemaRepository = mock(SchemaRepository.class);
    DownloadSchemaRegistryMojo downloadSchemaRegistryMojo = spy(new DownloadSchemaRegistryMojo());
    ReflectionTestUtils.setField(downloadSchemaRegistryMojo,"schemaRepository", schemaRepository);
    ReflectionTestUtils.setField(downloadSchemaRegistryMojo,"project", PROJECT_NAME);
    ReflectionTestUtils.setField(downloadSchemaRegistryMojo, "outputDirectory", new File("./"));

    List<Schema> schemas = new ArrayList<>();
    schemas.add(TEST_AVRO_SCHEMA);
    when(schemaRepository.list()).thenReturn(schemas);
    when(schemaRepository.fetch(anyString())).thenThrow(new StatusRuntimeException(Status.NOT_FOUND));

    assertThrows(MojoExecutionException.class, () -> downloadSchemaRegistryMojo.execute());
  }

  @Test
  void throwExceptionOnPubSubError() throws MojoExecutionException {
    SchemaRepository schemaRepository = mock(SchemaRepository.class);
    DownloadSchemaRegistryMojo downloadSchemaRegistryMojo = spy(new DownloadSchemaRegistryMojo());
    ReflectionTestUtils.setField(downloadSchemaRegistryMojo,"schemaRepository", schemaRepository);
    ReflectionTestUtils.setField(downloadSchemaRegistryMojo,"project", PROJECT_NAME);
    ReflectionTestUtils.setField(downloadSchemaRegistryMojo, "outputDirectory", new File("./"));

    List<Schema> schemas = new ArrayList<>();
    schemas.add(TEST_AVRO_SCHEMA);
    when(schemaRepository.list()).thenReturn(schemas);
    when(schemaRepository.fetch(anyString())).thenReturn(TEST_AVRO_SCHEMA);

    downloadSchemaRegistryMojo.execute();

    File file = new File("./test-project/an-avro-schema-name.avsc");
    assertTrue(file.exists());
  }

  @Test
  void shouldNotExecuteIfSkipped() throws MojoExecutionException {
    SchemaServiceClient schemaServiceClient = mock(SchemaServiceClient.class);
    DownloadSchemaRegistryMojo downloadSchemaRegistryMojo = spy(new DownloadSchemaRegistryMojo());
    ReflectionTestUtils.setField(downloadSchemaRegistryMojo,"client",schemaServiceClient);
    ReflectionTestUtils.setField(downloadSchemaRegistryMojo,"skip", true);
    downloadSchemaRegistryMojo.execute();
    verify(schemaServiceClient,times(0)).listSchemas(anyString());
    verify(downloadSchemaRegistryMojo, times(1)).getLog();
  }

  @Test
  void shouldSetClient() {
    SchemaServiceClient schemaServiceClient = mock(SchemaServiceClient.class);
    DownloadSchemaRegistryMojo downloadSchemaRegistryMojo = new DownloadSchemaRegistryMojo();
    ReflectionTestUtils.setField(downloadSchemaRegistryMojo,"client",schemaServiceClient);
  }

  @Test
  void shouldCloseClientWhenCallingClose() throws IOException {
    SchemaServiceClient schemaServiceClient = mock(SchemaServiceClient.class);
    DownloadSchemaRegistryMojo downloadSchemaRegistryMojo = new DownloadSchemaRegistryMojo();
    ReflectionTestUtils.setField(downloadSchemaRegistryMojo,"client",schemaServiceClient);
    downloadSchemaRegistryMojo.close();
    verify(schemaServiceClient,times(1)).close();
  }

}