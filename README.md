usage

```
mvn verify
java -jar target/quick-0.0.1-SNAPSHOT.jar # reads classpath foo-local.properties by default
EDMS_DFC_PROPERTIES_PATH=/foo-dev.properties java -jar target/quick-0.0.1-SNAPSHOT.jar
EDMS_DFC_PROPERTIES_PATH=/Users/sdoxsee/foo-bing.properties java -jar target/quick-0.0.1-SNAPSHOT.jar
```