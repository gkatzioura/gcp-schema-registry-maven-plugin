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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.pubsub.v1.Schema;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class PatternVersionsTest {

  @Test
  void shouldMatch() {
    List<Pattern> patterns = new ArrayList<>();
    patterns.add(Pattern.compile("a*b"));
    List<String> versions = new ArrayList<>();
    versions.add("123");
    PatternVersions patternVersions = new PatternVersions(patterns, versions);
    Schema schema = mock(Schema.class);
    when(schema.getName()).thenReturn("projects/test-project/schemas/aaaaaab");
    when(schema.getRevisionId()).thenReturn("123");


    Optional<String> optVersion = patternVersions.matches(schema);
    String version = optVersion.get();
    assertTrue(optVersion.isPresent());
    assertEquals("aaaaaab@123",version);
  }

  @Test
  void shouldMatchAnyVersionIfnoVersionsSpecified() {
    List<Pattern> patterns = new ArrayList<>();
    patterns.add(Pattern.compile("a*b"));
    PatternVersions patternVersions = new PatternVersions(patterns, new ArrayList<>());
    Schema schema = mock(Schema.class);
    when(schema.getName()).thenReturn("projects/test-project/schemas/aaaab");
    when(schema.getRevisionId()).thenReturn("123");

    Optional<String> optVersion = patternVersions.matches(schema);
    assertTrue(optVersion.isPresent());
    String version = optVersion.get();


    Optional<String> revision = patternVersions.matches(schema);
    assertTrue(revision.isPresent());
    assertEquals("aaaab",version);
  }

}