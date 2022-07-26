package com.work.wushig.string;

import java.util.Arrays;
import java.util.List;

public class WStringUtils {


    /**
     * 判断字符串是否为空
     *
     * @param str 需要操作的字符串
     * @return str为null或者空字符串都返回true, 其他择返回false
     */
    public static boolean isBlack(String str) {
        return str == null || "".equals(str);
    }

    /**
     * 判断字符串trim之后是否为空
     *
     * @param str 需要操作的字符串
     * @return str为null或者trim之后为空字符串都返回true, 其他择返回false
     */
    public static boolean isBlack4Trim(String str) {
        if (str == null) return true;
        return "".equals(str.trim());
    }

    /**
     * 判断字符串是否不为空
     *
     * @param str 需要操作的字符串
     * @return str为null或者空字符串都返回false, 其他择返回true
     */
    public static boolean isNotBlack(String str) {
        return !isBlack(str);
    }

    /**
     * 判断字符串trim之后是否不为空
     *
     * @param str 需要操作的字符串
     * @return str为null或者trim之后为空字符串都返回false, 其他择返回true
     */
    public static boolean isNotBlack4Trim(String str) {
        return !isBlack4Trim(str);
    }

    /**
     * 返回给定参数，如果为null或者空字符串则返回空字符串
     *
     * @param str 需要操作的字符串
     * @return
     */
    public static String getOrEmpty(String str) {
        return isBlack(str) ? "" : str;
    }

    /**
     * 回给定参数，如果为null或者trim之后为空字符串则返回空字符串
     *
     * @param str 需要操作的字符串
     * @return
     */
    public static String getOrEmpty4Trim(String str) {
        return isBlack4Trim(str) ? "" : str;
    }

    /**
     * 判断对象，如果是null，返回空字符串，否则返回原对象
     * 此方法转换的对象必须可以进行toString，
     * 必要时，可以根据自身需要，重写其toString方法实现自己的业务
     * @param o 操作的对象
     * @return
     */
    public static String getOrEmpty(Object o) {
        return o == null ? "" : o.toString();
    }

    /**
     * 将字符串分割，并转换为ArrayList
     *
     * @param str       需要操作的字符串
     * @param partition 分割符
     * @return
     */
    public static List<String> splitToList(String str, String partition) {
        return Arrays.asList(str.split(partition));
    }
}
