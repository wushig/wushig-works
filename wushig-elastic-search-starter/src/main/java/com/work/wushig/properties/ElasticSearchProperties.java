package com.work.wushig.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Auther: gaojianjun
 * @Date: 2022-05-12 11:03
 * @projectName: downloadUtils
 * @Description:
 */
@ConfigurationProperties(prefix = "wushig.elastic")
@Component
public class ElasticSearchProperties {

    /**
     * 是否开启wushig-elastic
     */
    private boolean enable;

    /**
     * 用户名
     */
    private String elasticUser;

    /**
     * 密码
     */
    private String elasticPassword;

    /**
     * 主机地址
     */
    private String hostName;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * 请求头
     */
    private String header;
    /**
     * 超时时间
     */
    private Integer timeOut;

    private String defaultIndexName;


    public String getDefaultIndexName() {
        return defaultIndexName;
    }

    public void setDefaultIndexName(String defaultIndexName) {
        this.defaultIndexName = defaultIndexName;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getElasticUser() {
        return elasticUser;
    }

    public void setElasticUser(String elasticUser) {
        this.elasticUser = elasticUser;
    }

    public String getElasticPassword() {
        return elasticPassword;
    }

    public void setElasticPassword(String elasticPassword) {
        this.elasticPassword = elasticPassword;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Integer getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Integer timeOut) {
        this.timeOut = timeOut;
    }
}
