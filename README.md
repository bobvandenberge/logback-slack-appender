# Logback Slack Appender
This is a Slack appender for [Logback](http://logback.qos.ch/), inspired on code of [logback-slack-appender](https://github.com/maricn/logback-slack-appender)

## Configuration
The minimum required configuration to get up and running is the following:

```

<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="true">
    <!-- Create the Slack appender -->
    <appender name="SLACK" class="com.seoduct.logging.SlackAppender">
        <webhookUri>YOUR_SLACK_URL</webhookUri>
        <channel>#general</channel>
        <username>Slack Appender</username>
        <iconEmoji>:computer:</iconEmoji>
    </appender>

    <!-- Hook up the Slack appender --> 
    <root level="ALL">
        <appender-ref ref="SLACK"/>
    </root>

</configuration>

```

### Running the appender asynchronous
By default the log appender will run synchronous. In most scenario's this is not what you want because now your process will have to wait on a Http call to finish.
Therefore you should setup the log appender to run asynchronously. You can do this with the following configuration:

```
    <!-- Create an asynchronous appender for the SLACK appender. -->
    <appender name="ASYNC_SLACK" class="ch.qos.logback.classic.AsyncAppender">
        <appender-ref ref="SLACK" />
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>WARN</level>
        </filter>
    </appender>

    <!-- Hook up the asynchrounous Slack appender --> 
    <root level="WARN">
        <appender-ref ref="ASYNC_SLACK" />
    </root>
```

### Changing the default format
It is possible to specify a different logging format by declaring a different layout: 

```
    <appender name="SLACK" class="com.seoduct.logging.SlackAppender">
        <layout class="ch.qos.logback.classic.PatternLayout">
            <pattern>*%-8level* ` %-36logger{36} ` - %msg %n ``` Stack trace: %ex{3} ```</pattern>
        </layout>
        <!-- Other fields removed for readability... -->
    </appender>
```