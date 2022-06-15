package com.work.wushig.connction;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.PropertyPreFilters;
import com.google.gwt.thirdparty.guava.common.base.CaseFormat;
import com.google.gwt.thirdparty.guava.common.base.Joiner;
import com.work.wushig.domain.ESEntity;
import com.work.wushig.domain.ESResult;
import com.work.wushig.exception.XmlNotFoundException;
import com.work.wushig.utils.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultAttribute;
import org.dom4j.tree.DefaultCDATA;
import org.dom4j.tree.DefaultElement;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Auther: gaojianjun
 * @Date: 2022-05-12 11:49
 * @projectName: downloadUtils
 * @Description:
 */
@Component
@Slf4j
public class WushigElasticConnector {

    @Autowired
    private RestClient wushigESClient;

    private String searchContent;

    private static Pattern REGREX_$ = Pattern.compile("\\$\\{(.*?)}");
    private static Pattern REGREX_BRACKETS = Pattern.compile("(?<=\\()(.+?)(?=\\))");
    private static Pattern REGREX_IF = Pattern.compile("([^#if]+)|(#[^#]+#end)");
    private static Pattern REGREX_QUOTATION = Pattern.compile("(?<=[\"|\'])(.*?)(?=[\"|\'])");
    private final static String REGREX_SPECIAL = "[\"~:\\[\\]^*!+?&|(){}\\\\]";

    private static String eq1 = "==";
    private static String eq2 = "===";
    private static String neq1 = "!=";
    private static String neq2 = "!==";

    private final String POST = "POST";
    private final String GET = "GET";
    private final String PUT = "PUT";

    private final String MAPPING = "/_mapping";
    private final String SEARCHING = "/_search";
    private final String UPDATE = "/_update";
    private final String DELETE = "/_delete_by_query";

    private final String SPLIT = "/";


    private String getXmlData(String location, String namespace) {
        Document doc = null;
        try {
            ClassPathResource resource = new ClassPathResource(location);
            final InputStream inputStream = resource.getInputStream();
            doc = DocumentHelper.parseText(inputStream2String(inputStream));
            Element rootElt = doc.getRootElement();
            final List roots = rootElt.content();
            final List<DefaultElement> defaultElements = (List<DefaultElement>) roots.stream().filter(e -> {
                return e instanceof DefaultElement;
            }).map(e -> {
                return (DefaultElement) e;
            }).collect(Collectors.toList());
            for (int i = 0; i < defaultElements.size(); i++) {
                DefaultElement defaultElement = defaultElements.get(i);
                final List attributes = defaultElement.attributes();
                DefaultAttribute defaultAttribute = (DefaultAttribute) attributes.get(0);
                final String value = defaultAttribute.getValue();
                if (namespace.equalsIgnoreCase(value)) {
                    final List contents = defaultElements.get(i).content();
                    for (Object o : contents) {
                        if (o instanceof DefaultCDATA) {
                            DefaultCDATA defaultCDATA = (DefaultCDATA) o;
                            return defaultCDATA.getText();
                        }
                    }
                }
            }
            return "";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new XmlNotFoundException("没有发现可用的xml文件");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String inputStream2String(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            byte[] b = new byte[10240];
            int n;
            while ((n = inputStream.read(b)) != -1) {
                outputStream.write(b, 0, n);
            }
        } catch (Exception e) {
            try {
                inputStream.close();
                outputStream.close();
            } catch (Exception e1) {
            }
        }
        return outputStream.toString();
    }


    private String resolveSearchContent(String sql, Map<String, String> param) {
        String res = "";
        //更换EL表达式  结构出${key}的内容
        Matcher m = REGREX_$.matcher(sql);
        StringBuffer sr = new StringBuffer();
        while (m.find()) {
            String group = m.group();
            final String key = group.replace("${", "").replace("}", "").trim();
            m.appendReplacement(sr, resolveSearchValue(param.getOrDefault(key, "")));
        }
        m.appendTail(sr);
        res = sr.toString();
        //处理if...end
        Matcher matcherIf = REGREX_IF.matcher(res);
        while (matcherIf.find()) {
            String ifContent = matcherIf.group();
            //处理判断条件
            Matcher m1 = REGREX_BRACKETS.matcher(ifContent);
            while (m1.find()) {
                String test = m1.group();
                final boolean resolveTest = resolveTest(test);
                if (!resolveTest) {
                    //不成立则移除整个代码块
                    res = res.replace(ifContent, "");
                } else {
                    //成立则移除if...end
                    String originContent = ifContent.replace("#if(" + test + ")", "").replace("#end", "");
                    res = res.replace(ifContent, originContent);
                }
            }
        }

        return res;
    }

    private boolean resolveTest(String test) {
        List<String> tests = new ArrayList<>();
        if (test.contains(eq2)) {
            tests = Arrays.asList(test.split(eq2));
            final Matcher matcher = REGREX_QUOTATION.matcher(tests.get(1));
            while (matcher.find()) {
                String test1 = matcher.group().trim();
                return tests.get(0).trim().equalsIgnoreCase(test1);
            }
        } else if (test.contains(neq2)) {
            tests = Arrays.asList(test.split(neq2));
            final Matcher matcher = REGREX_QUOTATION.matcher(tests.get(1));
            while (matcher.find()) {
                String test1 = matcher.group().trim();
                return !tests.get(0).trim().equalsIgnoreCase(test1);
            }
        } else if (test.contains(eq1)) {
            tests = Arrays.asList(test.split(eq1));
            final Matcher matcher = REGREX_QUOTATION.matcher(tests.get(1));
            while (matcher.find()) {
                String test1 = matcher.group().trim();
                return tests.get(0).trim().equalsIgnoreCase(test1);
            }
        } else if (test.contains(neq1)) {
            tests = Arrays.asList(test.split(neq1));
            final Matcher matcher = REGREX_QUOTATION.matcher(tests.get(1));
            while (matcher.find()) {
                String test1 = matcher.group().trim();
                return !tests.get(0).trim().equalsIgnoreCase(test1);
            }
        } else {
            throw new RuntimeException("分析xml中判断失败");
        }
        return false;
    }


    /**
     * @param xmlLocation xml地址
     * @param namespace   执行查询的xml的namespace
     * @param indexName   索引名称
     * @param param       参数
     * @param tClass      隐射的java类
     * @return
     * @Description 执行查询信息, 通过本方法执行查询ES
     */
    public synchronized <T> ESResult<T> execGet(String xmlLocation, String namespace, String indexName, Map<String, String> param, Class<T> tClass) {
        final long start = System.currentTimeMillis();
        StringBuffer logData = new StringBuffer();
        ESResult<T> res = new ESResult<>();
        this.searchContent = getXmlData(xmlLocation, namespace);
        final String paseEL = this.resolveSearchContent(searchContent, param);
        logData.append("\n当前查询信息为\n")
                .append("- 语句："+JSON.parseObject(paseEL).toJSONString()+"\n");
        try {
            Request request = new Request(GET, indexName + SEARCHING);
            // 设置其他一些参数比如美化json
            request.addParameter("pretty", "true");
            request.setJsonEntity(paseEL);
            Response response = wushigESClient.performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            final JSONObject jsonObject = (JSONObject) JSONObject.parse(responseBody);
            //处理查询时间问题
            logData.append("- elastic耗费查询时间："+jsonObject.getInteger("took")+"\n");
            res.setTook(jsonObject.getInteger("took"));
            //处理总数问题
            logData.append("- elastic查询总数："+jsonObject.getJSONObject("hits").getInteger("total")+"\n");
            res.setTotal(jsonObject.getJSONObject("hits").getInteger("total"));
            //处理查询列表
            final JSONArray jsonArray = jsonObject.getJSONObject("hits").getJSONArray("hits");
            final List<JSONObject> source = jsonArray.stream().map(e -> {
                final JSONObject sourceItem = ((JSONObject) e).getJSONObject("_source");
                sourceItem.put("_id", ((JSONObject) e).getString("_id"));
                return sourceItem;
            }).collect(Collectors.toList());
            List<T> list = new ArrayList<>();
            for (int i = 0; i < source.size(); i++) {
                final JSONObject item = source.get(i);
                final T t = JSON.parseObject(JSON.toJSONString(replaceKeyLow(item)), tClass);
                list.add(i, t);
            }
            res.setRows(list);
            //处理分组查询
            res.setAggregations(jsonObject.getJSONObject("aggregations"));
            final long end = System.currentTimeMillis();
            logData.append("- 本次查询耗费总时间:"+(end - start)+"\n").append("本次查询结束\n");
            log.info(MarkerFactory.getMarker("WUSHIG"),logData.toString());
            return res;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param indexName 新建索引名
     * @return HashMap<String, Object>
     * @Description 新建一个索引
     */
    public HashMap<String, Object> execAdd(String indexName) {
        try {
            Request request = new Request(PUT, indexName);
            Response response = wushigESClient.performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            return JSONObject.parseObject(responseBody, HashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param indexName 索引名
     * @param mapping   新建索引的配置和mapping信息
     * @param type      新增mapping的类型
     * @return
     * @Description 新建一个索引，并且配置该条索引
     */
    public String execAdd(String indexName, String type, String mapping) {
        try {
            Request request = new Request(PUT, indexName);
            wushigESClient.performRequest(request);
            log.info(MarkerFactory.getMarker("WUSHIG"),"索引建立完成：==========> {} ", indexName);
            request = new Request(POST, indexName + SPLIT + type + MAPPING);
            request.addParameter("pretty", "true");
            request.setJsonEntity(mapping);
            Response response = wushigESClient.performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            log.info(MarkerFactory.getMarker("WUSHIG"),"索引配置已完成：==========> {}", indexName);
            return responseBody;
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * @param indexName 索引名称
     * @param type      mapping类型
     * @param t         新增的实体
     * @return
     * @Description 新增或者修改某一条数据
     */
    public <T extends ESEntity> String execPut(String indexName, String type, T t) {
        try {
            if (!StringUtils.isEmpty(t.get_id())) {
                throw new RuntimeException("新增实体不能拥有_id字段");
            }
            //新增一条
            Request request = new Request(PUT, indexName + SPLIT + type + SPLIT + UUIDUtils.generateUUIDForIDs());
            final String sql = toJSONStringWithoutIdAndWithNull(t);
            request.addParameter("pretty", "true");
            request.setJsonEntity(sql);
            Response response = wushigESClient.performRequest(request);
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * @param indexName 索引名称
     * @param type      mapping类型
     * @param t         新增的实体
     * @return
     * @Description 修改某一条数据
     */
    public <T extends ESEntity> String execPost(String indexName, String type, T t) {
        try {
            if (StringUtils.isEmpty(t.get_id())) {
                throw new RuntimeException("修改实体必须拥有_id字段");
            }
            Request request = new Request(POST, indexName + SPLIT + type + SPLIT + t.get_id() + UPDATE);
            final String sql = toJSONStringWithoutIdAndNull(t);
            HashMap<String, Object> param = new HashMap<>();
            param.put("doc", JSONObject.parseObject(sql));
            request.addParameter("pretty", "true");
            request.setJsonEntity(JSON.toJSONString(param));
            Response response = wushigESClient.performRequest(request);
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * @param indexName 索引名
     * @param id        需要删除的数据ID
     * @return
     * @Description 删除某一条数据
     */
    public String execDelete(String indexName, String id) {
        try {
            if (StringUtils.isEmpty(id)) {
                throw new RuntimeException("删除实体必须拥有_id字段");
            }
            Request request = new Request(POST, indexName + DELETE);
            HashMap<String, Object> param = new HashMap<>();
            param.put("query", new HashMap<String, Object>() {{
                put("term", new HashMap<String, String>() {{
                    put("_id", id);
                }});
            }});
            request.addParameter("pretty", "true");
            request.setJsonEntity(JSON.toJSONString(param));
            Response response = wushigESClient.performRequest(request);
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }


    private String toJSONStringWithoutIdAndWithNull(Object o){
        String[] excludeProperties = {"_id"};
        PropertyPreFilters filters = new PropertyPreFilters();
        PropertyPreFilters.MySimplePropertyPreFilter excludefilter = filters.addFilter();
        excludefilter.addExcludes(excludeProperties);
        SerializeConfig serializeConfig = new SerializeConfig();
        serializeConfig.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        return JSONObject.toJSONString(o,serializeConfig,excludefilter,SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue);
    }

    private String toJSONStringWithoutIdAndNull(Object o){
        String[] excludeProperties = {"_id"};
        PropertyPreFilters filters = new PropertyPreFilters();
        PropertyPreFilters.MySimplePropertyPreFilter excludefilter = filters.addFilter();
        excludefilter.addExcludes(excludeProperties);
        return JSONObject.toJSONString(o,excludefilter,SerializerFeature.PrettyFormat);
    }


    /*
     * @Author gaojianjun
     * @Description //将key值转变为驼峰形式
     * @Date 2021-09-01
     * @Param
     * @return
     **/
    private LinkedHashMap<String, Object> replaceKeyLow(JSONObject map) {
        LinkedHashMap re_map = new LinkedHashMap();
        if (re_map != null) {
            Iterator var2 = map.entrySet().iterator();

            while (var2.hasNext()) {
                Map.Entry<String, Object> entry = (Map.Entry) var2.next();
                re_map.put(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, entry.getKey()), map.get(entry.getKey()));
            }

            map.clear();
        }
        return re_map;
    }

    /*
     * @Author gaojianjun
     * @Description //字符串转换为驼峰形式
     * @Date 2021-09-01
     * @Param
     * @return
     **/
    private String humpToUnderline(String para) {
        StringBuilder sb = new StringBuilder(para);
        int temp = 0;//偏移量，第i个下划线的位置是 当前的位置+ 偏移量（i-1）,第一个下划线偏移量是0
        for (int i = 0; i < para.length(); i++) {
            if (Character.isUpperCase(para.charAt(i))) {
                sb.insert(i + temp, "_");
                temp += 1;
            }
        }
        return sb.toString().toLowerCase();
    }

    private String resolveSearchValue(String searchValueForNameOrNo) {
        String formatSearchValue = searchValueForNameOrNo.replaceAll(REGREX_SPECIAL, " ").replace(SPLIT, "\\\\\\\\/");
        return Joiner.on(" AND ").join(Arrays.asList(formatSearchValue.trim().split(" ")));
    }

}
