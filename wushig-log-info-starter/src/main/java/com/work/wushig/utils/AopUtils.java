package com.work.wushig.utils;

import com.alibaba.fastjson.JSONObject;
import com.work.wushig.domain.LogEntity;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.MarkerFactory;

import java.text.SimpleDateFormat;

/**
 * @Auther: gaojianjun
 * @Date: 2022-05-14 12:00
 * @projectName: downloadUtils
 * @Description:
 */
@Slf4j
public class AopUtils {

    public static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");

    public static void afterThrowing(JoinPoint joinPoint){
        log.warn(MarkerFactory.getMarker("WUSHIG"),"捕获到错误日志");
    }

    public static LogEntity around(ProceedingJoinPoint proceedingJoinPoint){
        LogEntity logEntity;
        LogEntity.LogEntityBuilder logEntityBuilder = null;
        long start = 0L;
        long end = 0L;
        Object result = null;
        try {
            Object[] args = proceedingJoinPoint.getArgs();
            start = System.currentTimeMillis();
            String arg = getParams(args);
            logEntityBuilder = LogEntity.builder()
                    .args(arg)
                    .methodLongName(proceedingJoinPoint.getSignature().toLongString())
                    .methodShortName(proceedingJoinPoint.getSignature().toShortString());
            result = getResult(proceedingJoinPoint);
            end = System.currentTimeMillis();
            String prettyInfo = "\n- ————————————wushig-log————————————\n" +
                    "- 方法名：\t\t"+proceedingJoinPoint.getSignature()+"  \n" +
                    "- 方法执行时长：\t"+(end-start)+"  ms\n" +
                    "- 方法参数：\t\t"+arg+"\n" +
                    "- 方法返回值：\t"+ (JSONObject.toJSONString(result).length()>1000?JSONObject.toJSONString(result).substring(0,1000):JSONObject.toJSONString(result)) +
                    "\n- ————————————wushig-log————————————\n\n";
            log.info(MarkerFactory.getMarker("WUSHIG"),prettyInfo);
            logEntity = logEntityBuilder
                    .executionTime(end-start)
                    .result(result)
                    .build();
        } catch (Throwable t) {
            log.warn(MarkerFactory.getMarker("WUSHIG"),"捕获到错误日志\n{}",t.getStackTrace());
            logEntity = logEntityBuilder
                    .executionTime(end-start)
                    .result(t)
                    .build();
        }
        return logEntity;
    }

    private static String getParams(Object[] args){
        String arg = "";
        try {
            arg = JSONObject.toJSONString(args);
        } catch (Exception e) {
            arg = "没有获取到参数信息";
        }
        return arg;
    }

    private static Object getResult(ProceedingJoinPoint proceedingJoinPoint) throws Exception{
        Object result = null;
        try {
            Object proceed = proceedingJoinPoint.proceed();
            result =  proceed;
        } catch (Throwable e) {
            result = "异常："+ e.getMessage();
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return result;
    }

}
