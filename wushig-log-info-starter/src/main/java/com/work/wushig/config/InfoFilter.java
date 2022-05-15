package com.work.wushig.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * @Auther: gaojianjun
 * @Date: 2022-05-15 02:11
 * @projectName: downloadUtils
 * @Description:
 */
public class InfoFilter extends Filter<ILoggingEvent> {
    Level level;

    private static final String WUSHIG = "WUSHIG";

    public InfoFilter() {
    }

    public FilterReply decide(ILoggingEvent event) {
        if (!this.isStarted()) {
            return FilterReply.NEUTRAL;
        } else {
            boolean hasMarker = event.getMarker() != null && WUSHIG.equals(event.getMarker().getName());
            if(!hasMarker &&  event.getLevel().isGreaterOrEqual(this.level)){
                return FilterReply.NEUTRAL;
            }
            return FilterReply.DENY;
        }
    }

    public void setLevel(String level) {
        this.level = Level.toLevel(level);
    }

    public void start() {
        if (this.level != null) {
            super.start();
        }

    }
}
