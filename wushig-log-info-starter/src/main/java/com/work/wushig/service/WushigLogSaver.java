package com.work.wushig.service;

import com.work.wushig.domain.LogEntity;
import com.work.wushig.enums.DATA_SOURCE_ENUM;
import com.work.wushig.properties.WushigLogInfoProperties;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @Auther: gaojianjun
 * @Date: 2022-05-14 13:37
 * @projectName: downloadUtils
 * @Description:
 */
@Component
@Slf4j
@ConditionalOnProperty(prefix = "wushig.log", value = "enable", havingValue = "true")
public class WushigLogSaver {

    @Autowired
    private WushigLogInfoProperties wushigLogInfoProperties;

    @Autowired
    private ApplicationContext applicationContext;


    /**
     * @Description 保存日志信息
     */
    @Async("asyncServiceExecutor")
    public void  executeSaveLog(LogEntity logEntity){
        /********************************************
         * 获取用户配置的类信息
         * 1、如果是注解产生的log日志，并且用户配置了单独的记录方法，则执行单独的记录方法
         * 2、执行公共的记录方法
         ********************************************/
        String recordClassInfo = wushigLogInfoProperties.getRecordLogClassInfo();
        if (logEntity.getData_source_enum().equals(DATA_SOURCE_ENUM.ANNOTATION) && !StringUtils.isEmpty(logEntity.getRecordClassInfo())) {
            recordClassInfo = logEntity.getRecordClassInfo();
        }
        final String currentThreadName = Thread.currentThread().getName();
        if(StringUtils.isEmpty(recordClassInfo)){
            log.info(MarkerFactory.getMarker("WUSHIG"),"{},用户没有配置记录日志的方法（recordClassInfo）,没有保存日志...", currentThreadName);
            return;
        }
        Object bean = applicationContext.getBean(recordClassInfo);
        if(!(bean instanceof WushigLogSaverProcesser)){
            log.error(MarkerFactory.getMarker("WUSHIG"),"{},记录日志的方法（recordClassInfo）不符合要求，必须实现WushigLogSaverProcesser类...",currentThreadName);
            return;
        }
        WushigLogSaverProcesser wushigLogSaverProcesser = (WushigLogSaverProcesser)bean;
        wushigLogSaverProcesser.saveLog(logEntity);
        log.info(MarkerFactory.getMarker("WUSHIG"),"保存日志成功");
    };
}
