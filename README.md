# ili2c-cli

## native image

```
./gradlew clean build fatjar
native-image --no-server --verbose --report-unsupported-elements-at-runtime --native-image-info -cp build/libs/ili2c-cli-5.1.1.jar -H:+ReportExceptionStackTraces
```