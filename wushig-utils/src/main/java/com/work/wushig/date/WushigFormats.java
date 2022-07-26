package com.work.wushig.date;

import java.text.SimpleDateFormat;

/**
 * @author gaojianjun
 * 定义了常用的时间转换格式
 */
public interface WushigFormats {


    /**
     * 时间转换yyyy-MM-dd格式
     */
    public final static String YYYYMMDD = "yyyy-MM-dd";

    /**
     * 时间转换yyyy-MM-dd hh:mm:ss格式
     */
    public final static String YYYYMMDD_HHMMSS = "yyyy-MM-dd hh:mm:ss";

    /**
     * 时间转换yyyy-MM-dd hh:mm:ss SSS格式
     */
    public final static String YYYYMMDD_HHMMSS_SSS = "yyyy-MM-dd hh:mm:ss SSS";

    /**
     * 转换器  到日期为止
     */
    public final static SimpleDateFormat FORMAT_DAY = new SimpleDateFormat(YYYYMMDD);

    /**
     * 转换器  到秒为止
     */
    public final static SimpleDateFormat FORMAT_SEC = new SimpleDateFormat(YYYYMMDD_HHMMSS);

    /**
     * 转换器  到毫秒为止
     */
    public final static SimpleDateFormat FORMAT_MIL = new SimpleDateFormat(YYYYMMDD_HHMMSS_SSS);

}
