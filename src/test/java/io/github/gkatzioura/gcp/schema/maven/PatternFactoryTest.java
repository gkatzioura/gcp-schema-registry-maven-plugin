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
import java.util.regex.Pattern;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Test;

class PatternFactoryTest {

  @Test
  void shouldCreatePattern() {
    Log log = mock(Log.class);
    PatternFactory patternFactory = new PatternFactory(log);
    List<String> patternsStr = new ArrayList<>();
    String pattern = "a*b";
    patternsStr.add(pattern);
    List<Pattern> patterns = patternFactory.create(patternsStr);
    assertEquals(pattern, patterns.get(0).pattern());
  }

  @Test
  void shouldThrowExceptionInvalidPattern() {
    Log log = mock(Log.class);
    PatternFactory patternFactory = new PatternFactory(log);
    List<String> patternsStr = new ArrayList<>();
    patternsStr.add("jsj^$^%^*(@)@(");
    assertThrows(IllegalStateException.class, () -> patternFactory.create(patternsStr));
  }

  @Test
  void shouldCreateIfVersionEmpty() {
    Log log = mock(Log.class);
    PatternFactory patternFactory = new PatternFactory(log);
    List<String> patternsStr = new ArrayList<>();
    String pattern = "a*b";
    patternsStr.add(pattern);

    List<String> versions = new ArrayList<>();

    patternFactory.create(patternsStr, versions);
  }

  @Test
  void shouldCreateMapIfSizeAndVersionEqual() {
    Log log = mock(Log.class);
    PatternFactory patternFactory = new PatternFactory(log);
    List<String> patternsStr = new ArrayList<>();
    String pattern = "a*b";
    patternsStr.add(pattern);

    List<String> versions = new ArrayList<>();
    versions.add("1");

    patternFactory.create(patternsStr, versions);
  }

  @Test
  void shouldCreateMapWithVersionNotPresent() {
    Log log = mock(Log.class);
    PatternFactory patternFactory = new PatternFactory(log);
    List<String> patternsStr = new ArrayList<>();
    String pattern = "a*b";
    patternsStr.add(pattern);

    List<String> versions = null;

    patternFactory.create(patternsStr, versions);
    /*
    assertFalse(patternVersions.isEmpty());

    PatternVersion first = patternVersions.get(0);
    assertEquals("a*b", first.getPattern().pattern());
    assertFalse(first.getVersion().isPresent());
     */
  }

  @Test
  void shouldMatchAllIfNoPatternsSpecified() {
    Log log = mock(Log.class);
    PatternFactory patternFactory = new PatternFactory(log);
    List<String> patternsStr = new ArrayList<>();

    PatternMatcher patternVersions = patternFactory.create(patternsStr, null);
    Schema mock = mock(Schema.class);
    when(mock.getName()).thenReturn("test");
    assertTrue(patternVersions.matches(mock).isPresent());
  }

}