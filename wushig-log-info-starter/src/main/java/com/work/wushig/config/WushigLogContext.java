package com.work.wushig.config;

import com.work.wushig.domain.LogEntity;
import org.springframework.stereotype.Component;

/**
 * @Auther: gaojianjun
 * @Date: 2022-05-14 13:44
 * @projectName: downloadUtils
 * @Description:
 */
@Component
public class WushigLogContext {
    private static ThreadLocal<LogEntity> threadLocal = new ThreadLocal<>();

    public static void setLogEntity(LogEntity logEntity){
        threadLocal.set(logEntity);
    }

    public static LogEntity logInfo(){
        return threadLocal.get();
    }

    public static void removeInfo(){
        threadLocal.remove();
    }
}
