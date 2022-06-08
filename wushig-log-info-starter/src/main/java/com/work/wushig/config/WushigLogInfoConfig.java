package com.work.wushig.config;

import com.work.wushig.properties.WushigLogInfoProperties;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Auther: gaojianjun
 * @Date: 2022-05-14 11:17
 * @projectName: downloadUtils
 * @Description:
 */
@Configuration
@EnableConfigurationProperties(WushigLogInfoProperties.class)
@ConditionalOnProperty(prefix = "wushig.log", value = "enable", havingValue = "true")
@Slf4j
@EnableAsync
public class WushigLogInfoConfig {


    @Autowired
    private  WushigLogInfoProperties wushigLogInfoProperties;

    public WushigLogInfoConfig() {
        // System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, "./logback-spring.xml");
        log.info(MarkerFactory.getMarker("WUSHIG"), "wushig-log-info-starter启动成功");
    }

    //写入一个线程池
    @Bean(name = "asyncServiceExecutor")
    public Executor asyncServiceExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(wushigLogInfoProperties.getMinPoolSize());
        //配置最大线程数
        executor.setMaxPoolSize(wushigLogInfoProperties.getMaxPoolSize());
        //配置队列大小
        executor.setQueueCapacity(wushigLogInfoProperties.getQueueCapacity());
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix(wushigLogInfoProperties.getNamePrefix());

        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        log.info(MarkerFactory.getMarker("WUSHIG"), "开启线程池（用于记录日志）成功");
        return executor;
    }
}
