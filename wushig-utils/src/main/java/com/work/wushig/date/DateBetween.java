package com.work.wushig.date;

public class DateBetween {

    /**
     * 日期间相差年份
     */
    private int yearBet;

    /**
     * 日期间相差月份
     */
    private int monthBet;

    /**
     * 日期间相差天数
     */
    private int dayBet;

    /**
     * 日期间相差小时
     */
    private int hourBet;

    /**
     * 日期间相差分钟
     */
    private int minBet;

    /**
     * 日期间相差秒数
     */
    private int secBet;

    /**
     * 日期间相差毫秒数
     */
    private int msBet;

    /**
     * 日期间总共相差天数，不足一天忽略
     */
    private long dayBetWithoutOthers;

    /**
     * 日期间总共相差小时数，不足一小时忽略
     */
    private long hourBetWithoutOthers;

    /**
     * 日期间总共相差分钟数，不足一分钟忽略
     */
    private long minBetWithoutOthers;

    /**
     * 日期间总共相差秒数，不足一秒忽略
     */
    private long secBetWithoutOthers;

    /**
     * 日期间总共相差毫秒数，不足一毫秒忽略
     */
    private long msBetWithoutOthers;


    public DateBetween() {
    }

    public DateBetween(int yearBet, int monthBet, int dayBet) {
        this.yearBet = yearBet;
        this.monthBet = monthBet;
        this.dayBet = dayBet;
    }

    public int getYearBet() {
        return yearBet;
    }

    public void setYearBet(int yearBet) {
        this.yearBet = yearBet;
    }

    public int getMonthBet() {
        return monthBet;
    }

    public void setMonthBet(int monthBet) {
        this.monthBet = monthBet;
    }

    public int getDayBet() {
        return dayBet;
    }

    public void setDayBet(int dayBet) {
        this.dayBet = dayBet;
    }

    public int getHourBet() {
        return hourBet;
    }

    public void setHourBet(int hourBet) {
        this.hourBet = hourBet;
    }

    public int getMinBet() {
        return minBet;
    }

    public void setMinBet(int minBet) {
        this.minBet = minBet;
    }

    public int getSecBet() {
        return secBet;
    }

    public void setSecBet(int secBet) {
        this.secBet = secBet;
    }

    public int getMsBet() {
        return msBet;
    }

    public void setMsBet(int msBet) {
        this.msBet = msBet;
    }

    public long getDayBetWithoutOthers() {
        return dayBetWithoutOthers;
    }

    public void setDayBetWithoutOthers(long dayBetWithoutOthers) {
        this.dayBetWithoutOthers = dayBetWithoutOthers;
    }

    public long getHourBetWithoutOthers() {
        return hourBetWithoutOthers;
    }

    public void setHourBetWithoutOthers(long hourBetWithoutOthers) {
        this.hourBetWithoutOthers = hourBetWithoutOthers;
    }

    public long getMinBetWithoutOthers() {
        return minBetWithoutOthers;
    }

    @Override
    public String toString() {
        return "DateBetween{" +
                "yearBet=" + yearBet +
                ", monthBet=" + monthBet +
                ", dayBet=" + dayBet +
                ", hourBet=" + hourBet +
                ", minBet=" + minBet +
                ", secBet=" + secBet +
                ", msBet=" + msBet +
                ", dayBetWithoutOthers=" + dayBetWithoutOthers +
                ", hourBetWithoutOthers=" + hourBetWithoutOthers +
                ", minBetWithoutOthers=" + minBetWithoutOthers +
                ", secBetWithoutOthers=" + secBetWithoutOthers +
                ", msBetWithoutOthers=" + msBetWithoutOthers +
                '}';
    }

    public void setMinBetWithoutOthers(long minBetWithoutOthers) {
        this.minBetWithoutOthers = minBetWithoutOthers;
    }

    public long getSecBetWithoutOthers() {
        return secBetWithoutOthers;
    }

    public void setSecBetWithoutOthers(long secBetWithoutOthers) {
        this.secBetWithoutOthers = secBetWithoutOthers;
    }

    public long getMsBetWithoutOthers() {
        return msBetWithoutOthers;
    }

    public void setMsBetWithoutOthers(long msBetWithoutOthers) {
        this.msBetWithoutOthers = msBetWithoutOthers;
    }
}
