package com.seoduct.logging;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Attachment {
    @NonNull
    private String text;

    @NonNull
    private Color color;
}
