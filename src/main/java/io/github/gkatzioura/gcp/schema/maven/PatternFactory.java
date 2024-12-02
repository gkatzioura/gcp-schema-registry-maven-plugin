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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.maven.plugin.logging.Log;

class PatternFactory {

  private final Log log;

  public PatternFactory(Log log) {
    this.log = log;
  }

  List<Pattern> create(List<String> patternsStr) {
    List<Pattern> patterns = new ArrayList<>();
    for (String subject : patternsStr) {
      patterns.add(createPattern(subject));
    }

    return patterns;
  }

  PatternMatcher create(List<String> patternsStr, List<String> versions) {
    if(patternsStr==null|| patternsStr.isEmpty()) {
      return PatternMatcher.matchAll();
    }

    if (versions != null && !versions.isEmpty() && patternsStr.size() != versions.size()) {
      throw new IllegalArgumentException(
          "Number of versions specified should be same as number of subjects");
    }

    List<Pattern> patterns = create(patternsStr);

    if (versions == null) {
      return new PatternVersions(patterns);
    }

    return new PatternVersions(patterns, versions);
  }

  private PatternVersions patternsWithVersions(List<String> patternsStr,
      List<String> versions) {
    List<Pattern> patterns = create(patternsStr);
    return new PatternVersions(patterns, versions);
  }

  private Pattern createPattern(String subject) {
    try {
      log.debug(String.format("Creating pattern for '%s'", subject));
      return Pattern.compile(subject);

    } catch (Exception ex) {
      throw new IllegalStateException(
          String.format("Exception thrown while creating pattern '%s'", subject),
          ex
      );
    }
  }

}
