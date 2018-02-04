package com.seoduct.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.util.StatusPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

public class SlackAppenderTest {

    private static final String SLACK = "SLACK";
    private Logger logger;

    @Before
    public void setup() {
        // We need to reload the configuration because in the Integration Step we overwrite
        // something in the integration. Because loggers are static, the same logger is used
        // for all tests. As we don't want tests to influence each other we reload the
        // configuration and create a new logger for each test explicitly.
        reloadLoggerConfiguration();

        logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    }

    @Test
    public void shouldLoadTheSlackAppender() {
        final Appender<ILoggingEvent> slackAppender = getAppender("SLACK");

        assertThat(slackAppender, is(not(nullValue())));
    }

    @Test
    public void shouldBeAbleToLoadSlackAppenderWithoutErrors() {
        final List<Status> statuses = getErrors();

        assertThat(statuses, is(empty()));
    }

    @Test
    public void shouldTakeOverConfigurationFromFile() {
        final SlackAppender slackAppender = getAppender(SLACK);

        assertThat(slackAppender.getWebhookUri(), is("https://fakeurl.com"));
        assertThat(slackAppender.getChannel(), is("#channel"));
        assertThat(slackAppender.getUsername(), is("username"));
        assertThat(slackAppender.getIconEmoji(), is(":emoji:"));
    }

    @Test
    public void shouldPostMessageToSlack() throws IOException, InterruptedException {
        try (MockWebServer server = new MockWebServer()) {
            // Assign
            setupMockServer(server);

            // Act
            logger.error("An error occured", new RuntimeException("EXCEPTION"));

            // Assert
            final Message message = getMessageFromMockServer(server);
            assertThat(message, is(equalTo(Message.builder()
                                                   .username("username")
                                                   .channel("#channel")
                                                   .iconEmoji(":emoji:")
                                                   .attachment(new Attachment("-- [ERROR]ROOT - An error occured", Color.RED))
                                                   .build())));
        }

        assertThat(getErrors(), is(empty()));
    }

    private Message getMessageFromMockServer(MockWebServer server) throws InterruptedException, IOException {
        final RecordedRequest recordedRequest = server.takeRequest(100, TimeUnit.MILLISECONDS);
        return new ObjectMapper().readValue(recordedRequest.getBody().inputStream(), Message.class);
    }

    private void setupMockServer(MockWebServer server) throws IOException {
        server.enqueue(new MockResponse().setBody("hello, world!"));
        server.start();
        String baseUrl = server.url("/slack/").toString();

        // Update the appender to post to the mock instead of slack
        final SlackAppender appender = getAppender(SLACK);
        appender.setWebhookUri(baseUrl);
    }

    private SlackAppender getAppender(String appenderName) {
        return (SlackAppender) logger.getAppender(appenderName);
    }

    private List<Status> getErrors() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        return context.getStatusManager().getCopyOfStatusList()
                .stream()
                .filter(status -> status.getLevel() == Status.ERROR)
                .collect(Collectors.toList());
    }

    private void reloadLoggerConfiguration() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        loggerContext.getStatusManager().clear();

        ContextInitializer ci = new ContextInitializer(loggerContext);
        URL url = ci.findURLOfDefaultConfigurationFile(true);

        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(loggerContext);
            loggerContext.reset();
            configurator.doConfigure(url);
        } catch (JoranException je) {
            // StatusPrinter will handle this
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
    }
}
