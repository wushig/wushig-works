package com.work.wushig.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Auther: gaojianjun
 * @Date: 2022-05-14 11:13
 * @projectName: downloadUtils
 * @Description:
 */
@ConfigurationProperties(prefix = "wushig.log")
@Component
public class WushigLogInfoProperties {

    /**
     * 开启配置
     */
    private boolean enable;

    /**
     * 是否记录所有controller日志
     */
    private boolean enableController;

    /**
     * 是否启用注解
     */
    private boolean enableAnnotation;

    /**
     * 记录日志的类的全路径，必须实现WushigLogSaverProcesser类
     */
    private String RecordLogClassInfo;

    /**
     * 用于记录日志的线程池核心线程数
     */
    private Integer minPoolSize = 2;

    /**
     * 用于记录日志的线程池最大线程数
     */
    private Integer maxPoolSize = 4;

    /**
     * 用于记录日志的线程池队列大小
     */
    private Integer queueCapacity = 9999;

    /**
     * 用于记录日志的线程池名称
     */
    private String namePrefix = "wushig-log-";

    public boolean getEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean getEnableController() {
        return enableController;
    }

    public void setEnableController(boolean enableController) {
        this.enableController = enableController;
    }

    public boolean getEnableAnnotation() {
        return enableAnnotation;
    }

    public void setEnableAnnotation(boolean enableAnnotation) {
        this.enableAnnotation = enableAnnotation;
    }

    public String getRecordLogClassInfo() {
        return RecordLogClassInfo;
    }

    public void setRecordLogClassInfo(String recordLogClassInfo) {
        RecordLogClassInfo = recordLogClassInfo;
    }

    public Integer getMinPoolSize() {
        return minPoolSize;
    }

    public void setMinPoolSize(Integer minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public Integer getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(Integer queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public String getNamePrefix() {
        return namePrefix;
    }

    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }
}
