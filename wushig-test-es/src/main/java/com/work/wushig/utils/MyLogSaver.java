package com.work.wushig.utils;

import com.work.wushig.domain.LogEntity;
import com.work.wushig.domain.MyLogEntity;
import com.work.wushig.service.WushigLogSaverProcesser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Auther: gaojianjun
 * @Date: 2022-05-14 14:50
 * @projectName: downloadUtils
 * @Description:
 */
@Slf4j
@Component
public class MyLogSaver implements WushigLogSaverProcesser {

    @Override
    public void saveLog(LogEntity logEntity) {
        MyLogEntity myLogEntity = new MyLogEntity();
        myLogEntity.setArgs(logEntity.getArgs());
        myLogEntity.setMethodLongName(logEntity.getMethodLongName());
        myLogEntity.setNote("123");
        log.info("记录了一次日志：{},{}，{}",myLogEntity.getMethodLongName(),myLogEntity.getArgs(),myLogEntity.getNote());
    }
}
