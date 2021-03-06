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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
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
            throw new XmlNotFoundException("?????????????????????xml??????");
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
        //??????EL?????????  ?????????${key}?????????
        Matcher m = REGREX_$.matcher(sql);
        StringBuffer sr = new StringBuffer();
        while (m.find()) {
            String group = m.group();
            final String key = group.replace("${", "").replace("}", "").trim();
            String paramOrDefault = param.getOrDefault(key, "");
            //????????????','???????????????????????????????????????????????????????????????????????????
            if(paramOrDefault.contains(",")){
                m.appendReplacement(sr, paramOrDefault);
            }else{
                m.appendReplacement(sr, resolveSearchValue(paramOrDefault));
            }

        }
        m.appendTail(sr);
        res = sr.toString();
        //??????if...end
        Matcher matcherIf = REGREX_IF.matcher(res);
        while (matcherIf.find()) {
            String ifContent = matcherIf.group();
            //??????????????????
            Matcher m1 = REGREX_BRACKETS.matcher(ifContent);
            while (m1.find()) {
                String test = m1.group();
                final boolean resolveTest = resolveTest(test);
                if (!resolveTest) {
                    //?????????????????????????????????
                    res = res.replace(ifContent, "");
                } else {
                    //???????????????if...end
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
            throw new RuntimeException("??????xml???????????????");
        }
        return false;
    }


    /**
     * @param xmlLocation xml??????
     * @param namespace   ???????????????xml???namespace
     * @param indexName   ????????????
     * @param param       ??????
     * @param tClass      ?????????java???
     * @return
     * @Description ??????????????????, ???????????????????????????ES
     */
    public synchronized <T> ESResult<T> execGet(String xmlLocation, String namespace, String indexName, Map<String, String> param, Class<T> tClass) {
        final long start = System.currentTimeMillis();
        StringBuffer logData = new StringBuffer();
        ESResult<T> res = new ESResult<>();
        this.searchContent = getXmlData(xmlLocation, namespace);
        final String paseEL = this.resolveSearchContent(searchContent, param);
        logData.append("\n- ????????????????????????????????????ES????????????????????????????????????????????????\n")
                .append("- elastic?????????\t\t\t"+JSON.parseObject(paseEL).toJSONString()+"\n");
        try {
            Request request = new Request(GET, indexName + SEARCHING);
            // ????????????????????????????????????json
            request.addParameter("pretty", "true");
            request.setJsonEntity(paseEL);
            Response response = wushigESClient.performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            final JSONObject jsonObject = JSON.parseObject(responseBody);//(JSONObject) JSONObject.parse(responseBody);
            //????????????????????????
            logData.append("- elastic?????????????????????\t"+jsonObject.getInteger("took")+"\n");
            res.setTook(jsonObject.getInteger("took"));
            //??????????????????
            JSONObject hits = jsonObject.getJSONObject("hits");
            logData.append("- elastic???????????????\t\t"+ hits.getInteger("total")+"\n");
            res.setTotal(hits.getInteger("total"));
            //??????????????????
            final JSONArray jsonArray = hits.getJSONArray("hits");
//            ForkJoinPool forkJoinPool = new ForkJoinPool(8);
//            List<T> list = forkJoinPool.submit(() -> jsonArray.parallelStream()
//                    .map(e->{
//                        JSONObject object = ((JSONObject) e);
//                        JSONObject sourceItem = object.getJSONObject("_source");
//                        synchronized (forkJoinPool){
//                            sourceItem.put("_id", object.getString("_id"));
//                            return JSON.parseObject(JSON.toJSONString(replaceKeyLow(sourceItem)), tClass);
//                        }
//                    }).collect(Collectors.toList())
//            ).fork().join();
//            forkJoinPool.shutdown();
            final List<T> list = jsonArray.parallelStream().map(e -> {
                JSONObject object = ((JSONObject) e);
                JSONObject sourceItem = object.getJSONObject("_source");
                sourceItem.put("_id", object.getString("_id"));
                T t = JSON.parseObject(JSON.toJSONString(replaceKeyLow(sourceItem)), tClass);
                return t;
            }).collect(Collectors.toList());
            res.setRows(list);
            //??????????????????
            res.setAggregations(jsonObject.getJSONObject("aggregations"));
            final long end = System.currentTimeMillis();
            logData.append("- ???????????????????????????:\t\t"+(end - start)+"\n").append("- ????????????????????????????????????ES????????????????????????????????????????????????\n");
            log.info(MarkerFactory.getMarker("WUSHIG"),logData.toString());
            return res;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param indexName ???????????????
     * @return HashMap<String, Object>
     * @Description ??????????????????
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
     * @param indexName ?????????
     * @param mapping   ????????????????????????mapping??????
     * @param type      ??????mapping?????????
     * @return
     * @Description ?????????????????????????????????????????????
     */
    public String execAdd(String indexName, String type, String mapping) {
        try {
            Request request = new Request(PUT, indexName);
            wushigESClient.performRequest(request);
            log.info(MarkerFactory.getMarker("WUSHIG"),"?????????????????????==========> {} ", indexName);
            request = new Request(POST, indexName + SPLIT + type + MAPPING);
            request.addParameter("pretty", "true");
            request.setJsonEntity(mapping);
            Response response = wushigESClient.performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            log.info(MarkerFactory.getMarker("WUSHIG"),"????????????????????????==========> {}", indexName);
            return responseBody;
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * @param indexName ????????????
     * @param type      mapping??????
     * @param t         ???????????????
     * @return
     * @Description ?????????????????????????????????
     */
    public <T extends ESEntity> String execPut(String indexName, String type, T t) {
        try {
            if (!StringUtils.isEmpty(t.get_id())) {
                throw new RuntimeException("????????????????????????_id??????");
            }
            //????????????
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
     * @param indexName ????????????
     * @param type      mapping??????
     * @param t         ???????????????
     * @return
     * @Description ?????????????????????
     */
    public <T extends ESEntity> String execPost(String indexName, String type, T t) {
        try {
            if (StringUtils.isEmpty(t.get_id())) {
                throw new RuntimeException("????????????????????????_id??????");
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
     * @param indexName ?????????
     * @param id        ?????????????????????ID
     * @return
     * @Description ?????????????????????
     */
    public String execDelete(String indexName, String id) {
        try {
            if (StringUtils.isEmpty(id)) {
                throw new RuntimeException("????????????????????????_id??????");
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
        SerializeConfig serializeConfig = new SerializeConfig();
        serializeConfig.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        return JSONObject.toJSONString(o,serializeConfig,excludefilter,SerializerFeature.PrettyFormat);
    }


    /*
     * @Author gaojianjun
     * @Description //???key????????????????????????
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
                re_map.put(humpToUnderline(entry.getKey()), map.get(entry.getKey()));
            }
            map.clear();
        }
        return re_map;
    }

    /*
     * @Author gaojianjun
     * @Description //??????????????????????????????
     * @Date 2021-09-01
     * @Param
     * @return
     **/
    private String humpToUnderline(String para) {
        StringBuilder sb = new StringBuilder(para);
        int length = sb.length();
        for (int i = 0; i < length - 1; i++) {
            if (sb.charAt(i) == 95) {
                char c = sb.charAt(i + 1);
                if (c > 96 && c < 123) {
                    sb.replace(i, i + 2, new String(new char[]{(char) (c - 32)}));
                } else {
                    sb.deleteCharAt(i);
                }
                length--;
            }
        }
        if (sb.charAt(length - 1) == 95) {
            sb.deleteCharAt(length - 1);
        }
        return sb.toString();
    }

    private String resolveSearchValue(String searchValueForNameOrNo) {
        String formatSearchValue = searchValueForNameOrNo.replaceAll(REGREX_SPECIAL, " ").replace(SPLIT, "\\\\\\\\/");
        return Joiner.on(" AND ").join(Arrays.asList(formatSearchValue.trim().split(" ")));
    }

}
