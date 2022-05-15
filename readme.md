### wushig-log-info-starter介绍

> 这个starter功能非常简单，仅仅只是用于记录日志并且格式化日志输出
> 
> 优点：
> 
> 1、开箱即用
> 
> 2、自带了一个可以输出彩色日志的logback.xml
> 
> 3、暴漏了用户自定义保存日志到数据库的接口，继承接口可以自己决定保存想要的信息到数据库

#### 使用方法

1. 下载代码，并且**install**到本地**maven**仓库
2. 使用maven引入本starter
   ```xml
       ...
       <dependencies>
           ...
           <dependency>
               <groupId>com.work.wushig</groupId>
               <artifactId>wushig-log-info-starter</artifactId>
               <version>1.0-SNAPSHOT</version>
           </dependency>
           ...
       </dependencies>
   
   ```

3. 配置使用环境
   > 另外还有一些关于线程池的配置可以在代码提示中看到，可以自己看看
   
   ![image](https://user-images.githubusercontent.com/29161527/168480893-8bb63c1f-5575-4379-9fa2-f1e07f10fe37.png)
   ```yaml
   wushig:
     log:
       enable: true  #启用本插件
       enableController: true #允许记录controller日志，开启则可以记录所有controller的运行日志
       enableAnnotation: true #允许记录注解日志，开启则记录任何标注了注解的日志
       RecordLogClassInfo: myLogSaver  #对日志进行处理的类，必须实现WushigLogSaverProcesser接口
   
   ```

<br/>

<br/>

4. 启动项目即可看到效果
   
   ![截图](45afbd8a864e991db31ebca19cbae367.png)
   
   ![截图](12bf264da0ce71b37a4973759f4b6a29.png)
   > 标注了**wushig-log-info-starter**的日志即为本插件输出的日志
   > 
   > **并且所有的日志都为彩色日志**

<br/>

5. 描述
   > @RecordLog  注解可用来标准需要记录日志的**方法**上，同样适用于**实现WushigLogSaverProcesser接口**的类

<br/>

6. **WushigLogSaverProcesser**接口
   ```java
   /**
    * 实现WushigLogSaverProcesser接口，默认需要实现它的saveLog方法，此方法可以获取到当前正在调用的方法的
    * 基本信息，这是线程安全的，并且本插件使用了一个小型的线程池去异步存储日志，不会对方法执行产生延迟
    * 效应
    */
   public class MyLogSaver implements WushigLogSaverProcesser {
   
       @Override
       public void saveLog(LogEntity logEntity) {
           MyLogEntity myLogEntity = new MyLogEntity();
           myLogEntity.setArgs(logEntity.getArgs());
           myLogEntity.setMethodLongName(logEntity.getMethodLongName());
           myLogEntity.setNote("123");
           log.info("记录了一次日志：{},{}，{}",myLogEntity.getMethodLongName(),myLogEntity.getArgs(),myLogEntity.getNote());
       }
   }
   ```

<br/>

### wushig-elastic-search-starter介绍

> 用于进行一些简单的ES操作
> 
> 优点：
> 
> 1、开箱即用
> 
> 2、提供了一些简单的接口去操作ES增删改查

<br/>

#### 使用方法

1. 下载代码，并且**install**到本地**maven**仓库
2. 使用maven引入本starter
   ```xml
       ...
       <dependencies>
           ...
           <dependency>
               <groupId>com.work.wushig</groupId>
               <artifactId>wushig-elastic-search-starter</artifactId>
               <version>1.0-SNAPSHOT</version>
           </dependency>
           ...
       </dependencies>
   ```

3. 配置使用环境
   ```yaml
   wushig:
     elastic:
       enable: true  #启用本插件
       hostName: 192.168.0.17  #ES的host地址
       port: 9200
   ```

4. 编写xml文件
   > 支持${XXXX}替换文件，总体是json的ES查询条件
   > 
   > 也支持if条件判断
   ```xml
   
   <?xml version="1.0" encoding="gb2312"?>
   <properties>
       <!--  使用${XXX}替换查询字段  -->
       <property name="XXXX">
           <![CDATA[{
                 "query": {
                   "term": {
                     "XXXXX.keyword": {
                       "value": "${XXX}"
                     }
                   }
                 },
                 "size": 0,
                 "aggs": {
                   "XXXXX": {
                     "terms": {
                       "field": "XXXXX"
                     }
                   }
                 }
           }]]>
       </property>
       
       <!--  使用if(${XXXXX} != '')判断是否执行  -->
       <property name="XXXXX">
           <![CDATA[{
             "query": {
               "bool": {
                 "must": [
                   #if(${XXXXX} != '')
                   {
                     "query_string": {
                       "fields": [
                         "XXXXX"
                       ],
                       "query": "${XXXXX}",
                       "type": "phrase"
                     }
                   },
                   #end
                   #if(${XXXXX} != '')
                   {
                     "term": {
                       "XXXXX": {
                         "value": ${XXXXX}
                       }
                     }
                   },
                   #end
                   #if(${XXXXX} != '')
                   {
                     "term": {
                       "XXXXX": {
                         "value": ${XXXXX}
                       }
                     }
                   },
                   #end
                   {
                     "exists": {
                       "field": "XXXXX"
                     }
                   }
                 ]
               }
             },
             "from": 0,
             "size": 10000
           }]]>
       </property>
   </properties>
   ```
5. 开始调用方法
   > 具体方法参数可以进项目查看，这里只说明方法基本功能
   > 
   > ![截图](a41343393eab624b9ddf55e8325b2b57.png)
   ```java
   public class TestEsController {
   
       //注入WushigElasticConnector类即可使用
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
           //执行查询功能
           final HashMap<String, Object> stringObjectHashMap = wushigElasticConnector.execGet("/elastic/dmsCommonUsedTopicalMapper.xml",
                   "getTopicalList",
                   "dms_common_used_topical",
                   param,
                   CommonUsedTopical.class);
           return JSON.toJSONString(stringObjectHashMap);
       }
       @RequestMapping("testAdd")
       public String testAdd(){
           //执行新增一个索引，并配置它的mapping信息
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
          //执行新增一条数据到索引中
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
           //执行修改索引中一条数据
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
           //执行删除索引中一条数据
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
   ```
