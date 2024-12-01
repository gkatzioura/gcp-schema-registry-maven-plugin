
package io.github.gkatzioura.gcp.schema.maven;

import static org.mockito.Mockito.mock;

import com.google.cloud.pubsub.v1.SchemaServiceClient;
import com.google.cloud.pubsub.v1.SchemaServiceClient.ListSchemasPagedResponse;
import com.google.pubsub.v1.ListSchemasRequest;
import com.google.pubsub.v1.ProjectName;
import com.google.pubsub.v1.Schema;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugin.logging.Log;

public class GooglePubsu {

  public static void main(String[] args) throws Exception {
    try (SchemaServiceClient schemaServiceClient = SchemaServiceClient.create()) {
      SchemaRepository schemaRepository = new SchemaRepository(mock(Log.class),schemaServiceClient,ProjectName.of("bigquerttest"));

      Schema schema = schemaRepository.fetch("projects/bigquerttest/schemas/an-avro-schema-name@f81ba5ff");

      System.out.println("kak");
    }

  }

  public static void mainc(String[] args) throws IOException {

    try (SchemaServiceClient schemaServiceClient = SchemaServiceClient.create()) {
      Schema schema = schemaServiceClient.getSchema(
          "projects/bigquerttest/schemas/dssd-97979._~+djd");
      System.out.println("my-schema");

    }
  }
  public static void mainb(String[] args) throws IOException {

    try(SchemaServiceClient schemaServiceClient = SchemaServiceClient.create()) {
      Schema schema = schemaServiceClient.getSchema("projects/bigquerttest/schemas/an-avro-schema-name");
      System.out.println("my-schema");
    }
  }

  public static void maina(String[] args) throws IOException {


    try(SchemaServiceClient schemaServiceClient = SchemaServiceClient.create()) {

      ListSchemasRequest listSchemasRequest = ListSchemasRequest.newBuilder()
          .setParent(ProjectName.of("bigquerttest").toString())
          .build();
      ListSchemasPagedResponse listSchemasPagedResponse = schemaServiceClient.listSchemas(
          ProjectName.of("bigquerttest"));

      List<Schema> schemas =  new ArrayList<>();
      for(Schema schema: listSchemasPagedResponse.iterateAll()) {
        schemas.add(schema);
      }

      System.out.println("no schemas");
    }


  }

}
