package com.seoduct.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.LayoutBase;

public class DefaultSlackLayout extends LayoutBase<ILoggingEvent> {
    @Override
    public String doLayout(ILoggingEvent event) {
        return "-- [" + event.getLevel() + "]" +
                event.getLoggerName() + " - " +
                event.getFormattedMessage();
    }
}
