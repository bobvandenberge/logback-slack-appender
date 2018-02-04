package com.seoduct.logging;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.Getter;
import lombok.Setter;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Appender that will send errors to slack
 */
@Setter
@Getter
public class SlackAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    /**
     * The webhook url that this appender will post the error message to. You can find this in
     * the Slack dashboard at https://[WORKSPACE].slack.com/apps/manage/custom-integrations
     */
    private String webhookUri;

    /**
     * The username under which this Appender will posts the messages
     */
    private String username;

    /**
     * The channel this appender will post the messages in. Accepts
     * both #channel and @user channels.
     */
    private String channel;

    /**
     * The emoji that this appender will use as icon. A supported list of emoticons can
     * be found here: https://www.webpagefx.com/tools/emoji-cheat-sheet/
     */
    private String iconEmoji;

    /**
     * The layout for the message that will be send. Default is {@link DefaultSlackLayout}.
     */
    private Layout<ILoggingEvent> layout = new DefaultSlackLayout();

    /**
     * Write the message to Slack
     *
     * @param event The event containing the error information
     */
    @Override
    protected void append(final ILoggingEvent event) {
        try {
            final RequestBody request = convertToRequestBody(event);
            executeRequest(request);
        } catch (IOException e) {
            addError("Error posting log to Slack: " + event, e);
        }
    }

    private Response executeRequest(RequestBody request) throws IOException {
        final OkHttpClient okHttpClient = new OkHttpClient();
        final Call call = okHttpClient
                .newCall(new Request.Builder()
                                 .url(webhookUri)
                                 .post(request)
                                 .build()
                );
        final Response response = call.execute();
        response.close();
        return response;
    }

    private RequestBody convertToRequestBody(ILoggingEvent event) throws JsonProcessingException {
        final String text = layout.doLayout(event);
        final Message.MessageBuilder message = Message.builder()
                .username(username)
                .channel(channel)
                .iconEmoji(iconEmoji)
                .attachment(new Attachment(text, getColor(event.getLevel())));

        return RequestBody.create(MediaType.parse("application/json"), new ObjectMapper().writeValueAsBytes(message.build()));
    }

    private Color getColor(Level level) {
        switch (level.levelInt) {
            case Level.ERROR_INT:
                return Color.RED;
            case Level.WARN_INT:
                return Color.YELLOW;
            default:
                return Color.GREEN;
        }
    }

}
