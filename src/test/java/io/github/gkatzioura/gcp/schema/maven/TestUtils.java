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

import com.google.protobuf.Timestamp;
import com.google.pubsub.v1.Schema;
import com.google.pubsub.v1.Schema.Type;

public class TestUtils {

  private TestUtils() {
  }

  static final String PROJECT_NAME = "test-project";

  static final String AVRO_SCHEMA_DEFINITION = "{\n"
      + " \"type\" : \"record\",\n"
      + " \"name\" : \"Avro\",\n"
      + " \"fields\" : [\n"
      + "   {\n"
      + "     \"name\" : \"StringField\",\n"
      + "     \"type\" : \"string\"\n"
      + "   },\n"
      + "   {\n"
      + "     \"name\" : \"FloatField\",\n"
      + "     \"type\" : \"float\"\n"
      + "   },\n"
      + "   {\n"
      + "     \"name\" : \"BooleanField\",\n"
      + "     \"type\" : \"boolean\"\n"
      + "   }\n"
      + " ]\n"
      + "}\n";

  static final String PROTO_SCHEMA_DEFINITION = "syntax = \"proto3\";\n"
      + "message ProtocolBuffer {\n"
      + "  string string_field = 1;\n"
      + "  float float_field = 2;\n"
      + "  bool boolean_field = 3;\n"
      + "}";

  static final Schema TEST_AVRO_SCHEMA = getSchema(Type.AVRO, AVRO_SCHEMA_DEFINITION,
      "projects/test-project/schemas/an-avro-schema-name@f81ba5ff");

  static final Schema TEST_PROTO_SCHEMA = getSchema(Type.PROTOCOL_BUFFER, PROTO_SCHEMA_DEFINITION,
      "projects/test-project/schemas/proto-schema-name@f81ba5ff");

  static Schema getSchema(Type type, String avroSchemaDefinition, String value) {
    return Schema.newBuilder()
        .setType(type)
        .setRevisionId("abcd")
        .setDefinition(avroSchemaDefinition)
        .setName(value)
        .setRevisionCreateTime(
            Timestamp.newBuilder().setSeconds(1731875834).setNanos(791000000).build())
        .build();
  }

}
