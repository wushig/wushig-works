package com.work.wushig.aspect;

import com.alibaba.fastjson.JSONObject;
import com.work.wushig.annotations.RecordLog;
import com.work.wushig.config.WushigLogContext;
import com.work.wushig.domain.LogEntity;
import com.work.wushig.enums.DATA_SOURCE_ENUM;
import com.work.wushig.service.WushigLogSaver;
import com.work.wushig.utils.AopUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: gaojianjun
 * @Date: 2022-05-14 11:01
 * @projectName: downloadUtils
 * @Description: 这是一个预定义的切面类，用于记录controller的日志信息
 */
@Aspect
@Slf4j
@Component
@ConditionalOnProperty(prefix = "wushig.log",value = "enable-controller",havingValue = "true")
public class RecordLogAspectForController implements RecordLogAspect{

    @Autowired
    private WushigLogSaver wushigLogSaver;

    /**
     * @Description 定义一个切点，用户自定义的时候，将以本切点对应的类记录
     */
    @Override
    @Pointcut("execution(public * *..*Controller.*(..))")
    public void pointCut(){}

    /**
     * @param joinPoint
     * @Description 当出现错误时的处理
     */
    @Override
    @AfterThrowing(pointcut = "pointCut()")
    public void afterThrowing(JoinPoint joinPoint) {
        AopUtils.afterThrowing(joinPoint);
    }

    /**
     * @param proceedingJoinPoint
     * @Description 环绕切面
     */
    @Override
    @Around("pointCut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) {
        WushigLogContext.removeInfo();
        final LogEntity logEntity = AopUtils.around(proceedingJoinPoint);
        logEntity.setData_source_enum(DATA_SOURCE_ENUM.ANNOTATION);
        logEntity.setRecordClassInfo("");
        WushigLogContext.setLogEntity(logEntity);
        wushigLogSaver.executeSaveLog(logEntity);
        if(logEntity.getResult() instanceof Exception){
            throw new RuntimeException((Exception)logEntity.getResult());
        }
        return logEntity.getResult();
    }

    /**
     * @param proceedingJoinPoint
     * @param recordLog
     * @Description 环绕切面, 使用注解的可以在这个方法中获取注解信息
     */
    @Override
    public Object around(ProceedingJoinPoint proceedingJoinPoint, RecordLog recordLog) {
        return null;
    }

    /**
     * @param args
     * @return 得到一个format好的参数信息
     * @Description 获取方法的参数信息，可以使用自己的方法获取
     */
    @Override
    public String getParams(Object[] args) {
        return null;
    }

    /**
     * @param proceedingJoinPoint
     * @return 得到一个处理好的返回信息
     * @Description 获取方法的输出信息，可以使用自己的方法获取
     */
    @Override
    public String getResult(ProceedingJoinPoint proceedingJoinPoint) {
        return null;
    }

    /**
     * @param result 上面方法中获得的结果
     * @param param  上面方法中获得的参数信息
     * @Description 保存日志的信息
     */
    @Override
    public void saveRequestLog(Object result, String param) {

    }
}
