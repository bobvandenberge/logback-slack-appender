package com.seoduct.logging;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;
import static java.util.Collections.EMPTY_LIST;

@NoArgsConstructor(access = AccessLevel.PRIVATE) // Required for Jackson
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class Message {
    private String text;
    private String username;
    private String channel;

    @JsonProperty("icon_emoji")
    private String iconEmoji;

    @Singular
    private List<Attachment> attachments = EMPTY_LIST;
}
