package com.work.wushig.controller;

import com.alibaba.fastjson.JSON;
import com.work.wushig.connction.WushigElasticConnector;
import com.work.wushig.domain.CommonUsedTopical;
import com.work.wushig.domain.ESResult;
import com.work.wushig.domain.Product;
import com.work.wushig.service.ITestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: gaojianjun
 * @Date: 2022-05-12 12:15
 * @projectName: downloadUtils
 * @Description:
 */
@RestController
@Slf4j
public class TestEsController {

    @Autowired
    private WushigElasticConnector wushigElasticConnector;

    @Autowired
    private ITestService testService;

    @RequestMapping("test")
    public String test(){
        testService.test();
        Map<String, String> param = new HashMap<>();
        param.put("pageFrom","0");
        param.put("pageSize","10");
        ESResult<CommonUsedTopical> commonUsedTopicalESResult = wushigElasticConnector.execGet("/elastic/dmsCommonUsedTopicalMapper.xml",
                "XXX",
                "XXXX",
                param,
                CommonUsedTopical.class);
        return JSON.toJSONString(commonUsedTopicalESResult);
    }
    @RequestMapping("testAdd")
    public String testAdd(){
        final String test = wushigElasticConnector.execAdd("test2","product","{\n" +
                "    \"product\": {\n" +
                "        \"properties\": {\n" +
                "            \"title\": {\n" +
                "                \"type\": \"text\",\n" +
                "                \"store\": \"true\"\n" +
                "            },\n" +
                "            \"description\": {\n" +
                "                \"type\": \"text\",\n" +
                "                \"index\": \"false\"\n" +
                "            },\n" +
                "            \"price\": {\n" +
                "                \"type\": \"double\"\n" +
                "            },\n" +
                "            \"onSale\": {\n" +
                "                \"type\": \"boolean\"\n" +
                "            },\n" +
                "            \"type\": {\n" +
                "                \"type\": \"integer\"\n" +
                "            },\n" +
                "            \"createDate\": {\n" +
                "                \"type\": \"date\"\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}");
        return JSON.toJSONString(test);
    }

    @RequestMapping("testPut")
    public String testPut(){
        Product product = new Product();
        product.setDescription("数据库的房间里卡设计了多款噶");
        product.setCreateDate(new Date());
        product.setOnSale(true);
        product.setPrice(25.8);
        product.setTitle("skjdflkasjgl;sajdkag");
        product.setType(1);
        return wushigElasticConnector.execPut("test2","product",product);
    }

    @RequestMapping("testPost")
    public String testPost(){
        Product product = new Product();
        product.setDescription("这是一条修改过的");
        // product.setCreateDate(new Date());
        // product.setOnSale(true);
        // product.setPrice(25.8);
        // product.setTitle("skjdflkasjgl;sajdkag");
        // product.setType(1);
        product.set_id("10494e8e75974025bfbdb562c2a87c9b");
        return wushigElasticConnector.execPost("test2","product",product);
    }

    @RequestMapping("testDelete")
    public String testDelete(){
        Product product = new Product();
        product.setDescription("这是一条修改过的");
        // product.setCreateDate(new Date());
        // product.setOnSale(true);
        // product.setPrice(25.8);
        // product.setTitle("skjdflkasjgl;sajdkag");
        // product.setType(1);
        product.set_id("10494e8e75974025bfbdb562c2a87c9b");
        return wushigElasticConnector.execDelete("test2","10494e8e75974025bfbdb562c2a87c9b");
    }
}
