package com.work.wushig.aspect;

import com.work.wushig.annotations.RecordLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @Auther: gaojianjun
 * @Date: 2022-05-14 11:30
 * @projectName: downloadUtils
 * @Description:
 */
public interface RecordLogAspect {

    /**
     * @Description 定义一个切点，用户自定义的时候，将以本切点对应的类记录
     */
    public void pointCut();

    /**
     * @Description 当出现错误时的处理
     * @param joinPoint
     */
    public void afterThrowing(JoinPoint joinPoint);


    /**
     * @Description 环绕切面
     * @param proceedingJoinPoint
     */
    public Object around(ProceedingJoinPoint proceedingJoinPoint);

    /**
     * @Description 环绕切面,使用注解的可以在这个方法中获取注解信息
     * @param proceedingJoinPoint
     */
    public Object around(ProceedingJoinPoint proceedingJoinPoint, RecordLog recordLog);

    /**
     * @Description 获取方法的参数信息，可以使用自己的方法获取
     * @param args
     * @return 得到一个format好的参数信息
     */
    public String getParams(Object[] args);

    /**
     * @Description 获取方法的输出信息，可以使用自己的方法获取
     * @param proceedingJoinPoint
     * @return 得到一个处理好的返回信息
     */
    public String getResult(ProceedingJoinPoint proceedingJoinPoint);

    /**
     * @Description 保存日志的信息
     * @param param 上面方法中获得的参数信息
     * @param result 上面方法中获得的结果
     */
    public void saveRequestLog(Object result,String param);
}
