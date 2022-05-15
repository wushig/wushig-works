package com.work.wushig.service;

import com.work.wushig.config.WushigLogContext;
import com.work.wushig.domain.LogEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Auther: gaojianjun
 * @Date: 2022-05-14 14:15
 * @projectName: downloadUtils
 * @Description:
 */
public interface WushigLogSaverProcesser {
    public void saveLog(LogEntity logEntity);
}
