# GCP Schemas maven plugin

A Maven plugin for Google Cloud Platform's Schemas Registry.

---


[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=gkatzioura_gcp-schema-registry-maven-plugin&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=gkatzioura_gcp-schema-registry-maven-plugin)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=gkatzioura_gcp-schema-registry-maven-plugin&metric=bugs)](https://sonarcloud.io/summary/new_code?id=gkatzioura_gcp-schema-registry-maven-plugin)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=gkatzioura_gcp-schema-registry-maven-plugin&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=gkatzioura_gcp-schema-registry-maven-plugin)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=gkatzioura_gcp-schema-registry-maven-plugin&metric=coverage)](https://sonarcloud.io/summary/new_code?id=gkatzioura_gcp-schema-registry-maven-plugin)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=gkatzioura_gcp-schema-registry-maven-plugin&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=gkatzioura_gcp-schema-registry-maven-plugin)

---

## Purpose
The purpose of GCP Schemas maven plugin is to download the schemas hosted in more than one GCP projects.
On a maven project using the plugin you can download the schemas locally.
This makes it feasible to generate the corresponding Java models using a code generation plugin.

## Usage

### Download all schemas in a project

You need to include the plugin at the plugins section of your maven project.

```xml
      <plugin>
        <groupId>io.github.gkatzioura.gcp</groupId>
        <artifactId>gcp-schemas-maven-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
        <configuration>
          <project>gcp-project</project>
          <outputDirectory>src/main/avro</outputDirectory>
        </configuration>
      </plugin>
```

Specify the GCP project, that hosts the desired schemas and include the outputDirectory the schemas will be stored.
The output directory will be created if it does not exist.

To proceed on downloading the schemas you should issue:

```bash
mvn gcp-schemas:download
```

### Specify schema format

Google Cloud Platform currently has support for Apache Avro and
Protocol Buffers schemas.
You can specify to download only one specific schema.

```xml
      <plugin>
        <groupId>io.github.gkatzioura.gcp</groupId>
        <artifactId>gcp-schemas-maven-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
        <executions>
          <execution>
            <id>one</id>
            <phase>generate-sources</phase>
            <goals>
              <goal>download</goal>
            </goals>
            <configuration>
              <project>gcp-project-1</project>
              <outputDirectory>src/main/avro</outputDirectory>
              <schemaType>AVRO</schemaType>
            </configuration>
          </execution>
        </executions>
      </plugin>
```

Valid values for the `schemaType` field are AVRO and PROTOCOL_BUFFER.

### Download schemas based on regular expression

Your Schema naming conventions in a project may vary. 
Depending on how Schemas are organized you might want to download schemas based on a regular expression.
You can specify more than one regex pattern. 

```xml
      <plugin>
        <groupId>io.github.gkatzioura.gcp</groupId>
        <artifactId>gcp-schemas-maven-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
        <executions>
          <execution>
            <id>one</id>
            <phase>generate-sources</phase>
            <goals>
              <goal>download</goal>
            </goals>
            <configuration>
              <project>gcp-project-1</project>
              <outputDirectory>src/main/avro</outputDirectory>
              <subjectPatterns>.*an-avro-schema-name.*,.*an-avro-schema-name.*</subjectPatterns>              
            </configuration>
          </execution>
        </executions>
      </plugin>
```
Provided you want to download specific schemas you can provide a list with the exact schema names to be downloaded.

```xml
            <configuration>
              <project>gcp-project-1</project>
              <outputDirectory>src/main/avro</outputDirectory>
              <subjectPatterns>schema1,schema2,schema3</subjectPatterns>              
            </configuration>
```

### Download specific versions of a schema

In case you want to download specific versions of schemas you specify the schemas using `subjectPatterns`
and by using the `versions` provide the corresponding versions. 
The number of elements in `subjectPatterns` and `versions` should be the same.


```xml
      <plugin>
        <groupId>io.github.gkatzioura.gcp</groupId>
        <artifactId>gcp-schemas-maven-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
        <executions>
          <execution>
            <id>one</id>
            <phase>generate-sources</phase>
            <goals>
              <goal>download</goal>
            </goals>
            <configuration>
              <project>gcp-project-1</project>
              <outputDirectory>src/main/avro</outputDirectory>
              <schemaType>AVRO</schemaType>
              <subjectPatterns>schema1,schema2,schema3</subjectPatterns>
              <versions>113aca6b,69965687,e6dc8d46</versions>              
            </configuration>
          </execution>
        </executions>
      </plugin>
```
### Download schemas from multiple projects

It is feasible to download schemas from more than one project by providing multiple plugin executions.

```xml
      <plugin>
        <groupId>io.github.gkatzioura.gcp</groupId>
        <artifactId>gcp-schemas-maven-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
        <executions>
          <execution>
            <id>one</id>
            <phase>generate-sources</phase>
            <goals>
              <goal>download</goal>
            </goals>
            <configuration>
              <project>gcp-project-1</project>
              <outputDirectory>src/main/avro</outputDirectory>
            </configuration>
          </execution>
          <execution>
            <id>two</id>
            <phase>generate-sources</phase>
            <goals>
              <goal>download</goal>
            </goals>
            <configuration>
              <project>gcp-project-2</project>
              <outputDirectory>src/main/avro</outputDirectory>
            </configuration>
          </execution>
        </executions>
      </plugin>
```
Since the plugin execution was configured to take effect at the `generate-sources` phase, the command to download the schemas should `generate-sources`: 

```bash
 mvn generate-sources
```

## Example of generating schemas from an Avro Schema

You can combine the plugin with a schema model generation plugin. Here is an example on downloading Avro schemas from a GCP projects, and generate the Java classes from those Avro schemas.

```xml
      <plugin>
        <groupId>io.github.gkatzioura.gcp</groupId>
        <artifactId>gcp-schemas-maven-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
        <executions>
          <execution>
            <id>one</id>
            <phase>generate-sources</phase>
            <goals>
              <goal>download</goal>
            </goals>
            <configuration>
              <project>gcp-project-1</project>
              <outputDirectory>src/main/avro</outputDirectory>
              <schemaType>AVRO</schemaType>
            </configuration>
          </execution>
        </executions>
      </plugin>
      <plugin>
        <groupId>org.apache.avro</groupId>
        <artifactId>avro-maven-plugin</artifactId>
        <version>1.12.0</version>
        <executions>
          <execution>
            <phase>generate-sources</phase>
            <goals>
              <goal>schema</goal>
            </goals>
            <configuration>
              <sourceDirectory>${project.basedir}/src/main/avro/</sourceDirectory>
              <outputDirectory>${project.basedir}/src/main/java/</outputDirectory>
            </configuration>
          </execution>
        </executions>
      </plugin>
```