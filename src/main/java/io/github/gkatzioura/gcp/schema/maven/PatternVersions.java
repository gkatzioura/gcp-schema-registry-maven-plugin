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
import com.google.pubsub.v1.SchemaName;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternVersions implements PatternMatcher {

  private final List<Pattern> patterns;
  private final List<String> versions;

  public PatternVersions(List<Pattern> patterns, List<String> versions) {
    this.patterns = patterns;
    this.versions = versions;
  }

  public PatternVersions(List<Pattern> patterns) {
    this.patterns = patterns;
    this.versions = new ArrayList<>();
  }

  @Override
  public Optional<String> matches(Schema schema) {
    SchemaName schemaName = SchemaName.parse(schema.getName());
    for (int i = 0; i < patterns.size(); i++) {
      Pattern pattern = patterns.get(i);
      Matcher matcher = pattern.matcher(schemaName.getSchema());
      if (matcher.matches()) {

        if (versions.isEmpty()) {
          return Optional.of(schemaName.getSchema());
        } else {
          String s = versions.get(i);
          return Optional.of(schemaName.getSchema()+"@"+ s);
        }
      }
    }

    return Optional.empty();
  }

}
