package com.work.wushig.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.pattern.CompositeConverter;
import ch.qos.logback.core.spi.FilterReply;
import org.apache.logging.log4j.message.MessageFormatMessage;
import org.slf4j.Marker;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.boot.logging.logback.ColorConverter;
import org.springframework.stereotype.Component;

/**
 * @Auther: gaojianjun
 * @Date: 2022-05-15 00:11
 * @projectName: downloadUtils
 * @Description:
 */
@Component
public class WushigFilter extends Filter<ILoggingEvent> {

    private static final String WUSHIG = "WUSHIG";


    @Override
    public FilterReply decide(ILoggingEvent iLoggingEvent) {
        if (!this.isStarted()) {
            return FilterReply.NEUTRAL;
        } else {
            if(iLoggingEvent.getMarker() != null && WUSHIG.equals(iLoggingEvent.getMarker().getName())){
                return FilterReply.ACCEPT;
            }
            return FilterReply.DENY;
        }
    }


    public void start() {
        super.start();
    }
}
