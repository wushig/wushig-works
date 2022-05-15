package com.work.wushig.utils;

import com.alibaba.fastjson.JSON;
import com.google.gwt.thirdparty.guava.common.base.CaseFormat;

import java.util.*;

/**
 * @Auther: gaojianjun
 * @Date: 2022-05-12 11:20
 * @projectName: downloadUtils
 * @Description:
 */
public class ElasticSearchUtil {

    public static <T> List<T> covertMapToEntity(List<LinkedHashMap> map, Class<T> tClass){
        List<T> list = new ArrayList<>();
        for (int i = 0; i < map.size(); i++) {
            LinkedHashMap<String, Object> e = map.get(i);
            final T t = JSON.parseObject(JSON.toJSONString(replaceKeyLow(e)), tClass);
            list.add(i,t);
        }
        return list;
    }
    /*
     * @Author gaojianjun
     * @Description //将key值转变为驼峰形式
     * @Date 2021-09-01
     * @Param
     * @return
     **/
    public static LinkedHashMap<String, Object> replaceKeyLow(LinkedHashMap<String, Object> map){
        LinkedHashMap re_map = new LinkedHashMap();
        if(re_map != null) {
            Iterator var2 = map.entrySet().iterator();

            while(var2.hasNext()) {
                Map.Entry<String, Object> entry = (Map.Entry)var2.next();
                re_map.put(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,entry.getKey()), map.get(entry.getKey()));
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
    public static String humpToUnderline(String para){
        StringBuilder sb=new StringBuilder(para);
        int temp=0;//偏移量，第i个下划线的位置是 当前的位置+ 偏移量（i-1）,第一个下划线偏移量是0
        for(int i=0;i<para.length();i++){
            if(Character.isUpperCase(para.charAt(i))){
                sb.insert(i+temp, "_");
                temp+=1;
            }
        }
        return sb.toString().toLowerCase();
    }
}
