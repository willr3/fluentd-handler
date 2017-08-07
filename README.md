# fluentd-handler
A jboss-logging handler that sends log messages to fluentd

## Usage

```xml
<custom-handler name="FLUENTD" class="org.perf.fluentd.FluentdHandler" module="org.perf.fluentd.fluentdhandler">
    <properties>
        <property name="host" value="localhost"/>
        <property name="port" value="24224"/>
        <property name="prefix" value="app"/>
        <property name="tag" value="host1"/>
        <property name="messageMap" value="true"/>
    </properties>
</custom-handler>
```

- `host` is required (port will default to 24224).
- `messageMap` will send the message as a map instead of using the `Formatter`
- `prefix` and `tag` correspond to the fluentd `prefix` and `tag` fields

## Building
> gradle jar

## Deploying
the easiest way to deploy is through jboss-cli.
Substitute ${PROJECT_HOME} with your actual path to the project
> module add --name=org.perf.fluentd.fluentdhandler \\\
  --resource-delimiter=, \\\
  --resources= \\\
      ${PROJECT_HOME}/build/libs/fluent-logger-0.3.3.jar, \\\
      ${PROJECT_HOME}/build/libs/json-simple-1.1.1.jar, \\\
      ${PROJECT_HOME}/build/libs/msgpack-0.6.8.jar, \\\
      ${PROJECT_HOME}/build/libs/fluentd-1.0-SNAPSHOT.jar, \\\
  --dependencies= \\\
      org.javassist, \\\
      org.jboss.logmanager, \\\
      org.slf4j