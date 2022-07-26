package com.work.wushig.date;


import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class WDateUtils {

    /**
     * 判断日期是否为空
     *
     * @param date
     * @return
     */
    public static boolean isNullDate(Date date) {
        return date == null;
    }

    /**
     * 给定日期为空，则返回当前日期
     *
     * @param date
     * @return
     */
    public static Date getOrNow(Date date) {
        return isNullDate(date) ? new Date() : date;
    }

    /**
     * 返回yyyy-MM-dd hh:mm:ss格式的当前日期
     * @return
     */
    public static String getNow(){
        return WushigFormats.FORMAT_SEC.format(new Date());
    }

    /**
     * 返回给定日期的日期字符串  yyyy-MM-dd
     *
     * @param date
     * @param nullToNow true:当给定日期为null的时候，则返回当前日期的转换；false:当给定日期为null的时候，则返回空字符串；
     * @return
     */
    public static String toDayString(Date date, boolean nullToNow) {
        if (nullToNow) {
            return WushigFormats.FORMAT_DAY.format(getOrNow(date));
        }
        return isNullDate(date) ? "" : WushigFormats.FORMAT_DAY.format(date);
    }

    /**
     * 返回给定日期的秒级的字符串 yyyy-MM-dd hh:mm:ss
     *
     * @param date      参数为null,则返回当前日期转换后的数据
     * @param nullToNow true:当给定日期为null的时候，则返回当前日期的转换；false:当给定日期为null的时候，则返回空字符串；
     * @return
     */
    public static String toSecString(Date date, boolean nullToNow) {
        if (nullToNow) {
            return WushigFormats.FORMAT_SEC.format(getOrNow(date));
        }
        return isNullDate(date) ? "" : WushigFormats.FORMAT_DAY.format(date);
    }

    /**
     * 返回给定日期的毫秒级的字符串  yyyy-MM-dd hh:mm:ss SSS
     *
     * @param date      参数为null,则返回当前日期转换后的数据
     * @param nullToNow true:当给定日期为null的时候，则返回当前日期的转换；false:当给定日期为null的时候，则返回空字符串；
     * @return
     */
    public static String toMilString(Date date, boolean nullToNow) {
        if (nullToNow) {
            return WushigFormats.FORMAT_MIL.format(getOrNow(date));
        }
        return isNullDate(date) ? "" : WushigFormats.FORMAT_DAY.format(date);
    }

    /**
     * 比较两个日期的大小
     *
     * @param d1 日期
     * @param d2 日期
     * @return 返回
     * -1： d1小于d2
     * 0:   d1等于d2
     * 1:   d1大于d2
     */
    public static int compareDates(Date d1, Date d2) {
        return d1.compareTo(d1);
    }

    /**
     * 获取时间戳
     *
     * @param date 参数为null,则返回当前时间戳
     * @return
     */
    public static long getTimestamp(Date date) {
        return getOrNow(date).getTime();
    }

    /**
     * 时间戳转日期
     *
     * @param timestamp
     * @return
     */
    public static Date getDateFromTimestamp(long timestamp) {
        return new Date(timestamp);
    }


    /**
     * 计算两个日期之间的差值
     *
     * @param d1
     * @param d2
     * @return
     */
    public static DateBetween getDuration(Date d1, Date d2) {
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate dateTime1 = d1.toInstant().atZone(zoneId).toLocalDate();
        LocalDate dateTime2 = d2.toInstant().atZone(zoneId).toLocalDate();
        Period period = Period.between(dateTime1, dateTime2);
        DateBetween dateBetween = new DateBetween(period.getYears(), period.getMonths(), period.getDays());
        long from = 0L;
        long to = 0L;
        if (compareDates(d1, d2) > 0) {
            from = d2.getTime();
            to = d1.getTime();
        } else {
            from = d1.getTime();
            to = d2.getTime();
        }
        dateBetween.setDayBetWithoutOthers((int) ((to - from) / (1000 * 60 * 60 * 24)));
        dateBetween.setHourBetWithoutOthers((int) ((to - from) / (1000 * 60 * 60)));
        dateBetween.setMinBetWithoutOthers((int) ((to - from) / (1000 * 60)));
        dateBetween.setSecBetWithoutOthers((int) ((to - from) / (1000)));
        dateBetween.setMsBetWithoutOthers((int) ((to - from)));
        return dateBetween;
    }

    /**
     * 进行日期的加减
     *
     * @param date      给定操作日期
     * @param delta     变化的值，可以为负值，负值则往给定日期前面计算
     * @param date_noun 累加的单位
     * @return
     */
    public static Date datePlus(Date date, int delta, Date_Noun date_noun) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        switch (date_noun) {
            case MS:
                cal.add(Calendar.MILLISECOND, delta);
                break;
            case SEC:
                cal.add(Calendar.SECOND, delta);
                break;
            case MIN:
                cal.add(Calendar.MINUTE, delta);
                break;
            case HOUR:
                cal.add(Calendar.HOUR, delta);
                break;
            case DAY:
                cal.add(Calendar.DATE, delta);
                break;
            case MONTH:
                cal.add(Calendar.MONTH, delta);
                break;
            case YEAR:
                cal.add(Calendar.YEAR, delta);
                break;
        }
        return cal.getTime();
    }
}
