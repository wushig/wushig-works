package com.work.wushig.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Auther: gaojianjun
 * @Date: 2022-05-11 22:35
 * @projectName: downloadUtils
 * @Description:
 */
@ConfigurationProperties(prefix = "wushig.threadpool")
public class ThreadPoolProperties {

    private boolean enable;


    private Integer maxThreads;

    private Integer minThreads;


    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Integer getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(Integer maxThreads) {
        this.maxThreads = maxThreads;
    }

    public Integer getMinThreads() {
        return minThreads;
    }

    public void setMinThreads(Integer minThreads) {
        this.minThreads = minThreads;
    }
}
