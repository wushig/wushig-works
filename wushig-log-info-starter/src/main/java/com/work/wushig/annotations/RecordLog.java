package com.work.wushig.annotations;

import java.lang.annotation.*;

/**
 * @Auther: gaojianjun
 * @Date: 2022-05-14 10:56
 * @projectName: downloadUtils
 * @Description:用于标记哪些类需要记录日志,可携带recordClass参数用于标记使用哪个类进行日志记录
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RecordLog {
    String recordClassPath() default "";
}
