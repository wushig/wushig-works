package com.work.wushig.config;

import com.work.wushig.properties.ElasticSearchProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.*;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * @Auther: gaojianjun
 * @Date: 2022-05-12 11:24
 * @projectName: downloadUtils
 * @Description:
 */
@Configuration
@EnableCaching
@EnableConfigurationProperties(ElasticSearchProperties.class)
@ConditionalOnProperty(prefix = "wushig.elastic",value = "enable",havingValue = "true")
@Slf4j
public class WushigElasticConnectorConfig {

    @Autowired
    private ElasticSearchProperties elasticSearchProperties;

    @Bean(name = "wushigESClient")
    @Primary
    public RestClient getClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        // 如果有多个从节点可以持续在内部new多个HttpHost，参数1是ip,参数2是HTTP端口，参数3是通信协议
        RestClientBuilder clientBuilder = RestClient.builder(new HttpHost(elasticSearchProperties.getHostName(),
                elasticSearchProperties.getPort(),
                "http"));

        if(!StringUtils.isEmpty(elasticSearchProperties.getHeader())){
            // 设置请求头，每个请求都会带上这个请求头
            Header[] defaultHeaders = {new BasicHeader("header", "value")};
            clientBuilder.setDefaultHeaders(defaultHeaders);
        }

        // 设置超时时间，多次尝试同一请求时应该遵守的超时。默认值为30秒，与默认套接字超时相同。若自定义套接字超时，则应相应地调整最大重试超时
        // clientBuilder.setMaxRetryTimeoutMillis(elasticSearchProperties.getTimeOut()==null?6000:elasticSearchProperties.getTimeOut());

        // 设置监听器，每次节点失败都可以监听到，可以作额外处理
        clientBuilder.setFailureListener(new RestClient.FailureListener() {
            @Override
            public void onFailure(Node node) {
                super.onFailure(node);
                log.info(MarkerFactory.getMarker("WUSHIG"),"{}==节点失败了",node.getName());
            }
        });

        /* 配置节点选择器，客户端以循环方式将每个请求发送到每一个配置的节点上，
        发送请求的节点，用于过滤客户端，将请求发送到这些客户端节点，默认向每个配置节点发送，
        这个配置通常是用户在启用嗅探时向专用主节点发送请求（即只有专用的主节点应该被HTTP请求命中）
        */
        clientBuilder.setNodeSelector(NodeSelector.SKIP_DEDICATED_MASTERS);


        /* 配置异步请求的线程数量，Apache Http Async Client默认启动一个调度程序线程，以及由连接管理器使用的许多工作线程
        （与本地检测到的处理器数量一样多，取决于Runtime.getRuntime().availableProcessors()返回的数量）。线程数可以修改如下,
        这里是修改为1个线程，即默认情况
        */
        clientBuilder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                return httpAsyncClientBuilder.setDefaultIOReactorConfig(
                        IOReactorConfig.custom().setIoThreadCount(1).build()
                );
            }
        });

        /*
        配置请求超时，将连接超时（默认为1秒）和套接字超时（默认为30秒）增加，
        这里配置完应该相应地调整最大重试超时（默认为30秒），即上面的setMaxRetryTimeoutMillis，一般于最大的那个值一致即60000
        */
        clientBuilder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                // 连接5秒超时，套接字连接60s超时
                return requestConfigBuilder.setConnectTimeout(5000).setSocketTimeout(60000);
            }
        });

        /*
        如果ES设置了密码，那这里也提供了一个基本的认证机制，下面设置了ES需要基本身份验证的默认凭据提供程序
        */
        if(!StringUtils.isEmpty(elasticSearchProperties.getElasticUser())){
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(elasticSearchProperties.getElasticUser(), elasticSearchProperties.getElasticPassword()));
            clientBuilder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                    return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                }
            });
            /*
            上面采用异步机制实现抢先认证，这个功能也可以禁用，这意味着每个请求都将在没有授权标头的情况下发送，然后查看它是否被接受，
            并且在收到HTTP 401响应后，它再使用基本认证头重新发送完全相同的请求，这个可能是基于安全、性能的考虑
            */
            clientBuilder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                    // 禁用抢先认证的方式
                    httpClientBuilder.disableAuthCaching();
                    return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                }
            });
        }
        final RestClient restClient = clientBuilder.build();
        if(!StringUtils.isEmpty(elasticSearchProperties.getDefaultIndexName())){
            try {
                String setMaxResults = "{ \"index\": { \"max_result_window\": \"10000000\" } }";
                log.info(MarkerFactory.getMarker("WUSHIG"),"开始执行setMaxResults：==========>{}",setMaxResults);
                Request request = new Request("PUT",elasticSearchProperties.getDefaultIndexName()+"/_settings");
                // 设置其他一些参数比如美化json
                request.addParameter("pretty", "true");
                request.setJsonEntity(setMaxResults);
                Response response = restClient.performRequest(request);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("启动wushig-elastic-search-starter失败,默认的索引执行错误....");
            }
        }
        log.info(MarkerFactory.getMarker("WUSHIG"),"wushig-elastic-search-starter启动成功");
        // 最后配置好的clientBuilder再build一下即可得到真正的Client
        return restClient;
    }

}
