package com.seoduct.logging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Color {
    RED("danger"),
    GREEN("good"),
    YELLOW("warning");

    @JsonValue
    private String value;

    @JsonCreator
    public static Color fromValue(String value) {
        for (Color color : Color.values()) {
            if(color.value.equals(value)) {
                return color;
            }
        }

        throw new IllegalArgumentException(String.format("Couldn't parse Color [%s]", value));
    }
}
