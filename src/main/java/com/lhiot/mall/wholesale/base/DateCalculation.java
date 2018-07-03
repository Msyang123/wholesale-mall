package com.lhiot.mall.wholesale.base;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Locale;

/**
 * 计算月，周的第一天和最后一天，及前15天的日期等
 * @author lynn
 *
 */

public class DateCalculation {

    public static String localDate2Date(LocalDate localDate){
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
        Instant instant1 = zonedDateTime.toInstant();
        Date from = Date.from(instant1);
        return DateFormatUtil.format5(from);
    }
    
    //本周第一天
    public static String firstDayOfThisWeek(){
    	return getStartDayOfWeek(LocalDate.now());
    }
    
    //本周最后一天
    public static String lastDayOfThisWeek(){
    	return getEndDayOfWeek(LocalDate.now());
    }
    
    //上周第一天
    public static String firstDayOfLastWeek(){
    	return getStartDayOfWeek(LocalDate.now().minusWeeks(1));
    }
    
    //上周最后一天
    public static String lastDayOfLastWeek(){
    	return getEndDayOfWeek(LocalDate.now().minusWeeks(1));
    }
    
    //本月第一天
    public static String firstDayOfThisMonth(){
    	return getStartDayOfMonth(LocalDate.now());
    }
    
    //本月最后一天
    public static String lastDayOfThisMonth(){
    	return getEndDayOfMonth(LocalDate.now());
    }
    
    //上月第一天
    public static String firstDayOfLastMonth(){
    	return getEndDayOfMonth(LocalDate.now());
    }
    
    //上月最后一天
    public static String getStartDayOfMonth(){
    	return getEndDayOfMonth(LocalDate.now().minusMonths(1));
    }
    
    //获取几天前的日期
    public static String otherDay(int days){
    	return localDate2Date(LocalDate.now().minusDays(days));
    }
    
    //周第一天
    public static String getStartDayOfWeek(TemporalAccessor date) {
        TemporalField fieldISO = WeekFields.of(Locale.CHINA).dayOfWeek();
        LocalDate localDate = LocalDate.from(date);
        localDate=localDate.with(fieldISO, 1);
        return localDate2Date(localDate);
    }
    
    //周最后一天
    public static String getEndDayOfWeek(TemporalAccessor date) {
        TemporalField fieldISO = WeekFields.of(Locale.CHINA).dayOfWeek();
        LocalDate localDate = LocalDate.from(date);
        localDate=localDate.with(fieldISO, 7);
        Date d = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).plusDays(1L).minusNanos(1L).toInstant());
        return DateFormatUtil.format5(d);
    }
    
    //月的第一天
    public static String getStartDayOfMonth(LocalDate date) {
        LocalDate now = date.with(TemporalAdjusters.firstDayOfMonth());
        return localDate2Date(now);
    }
    
    //月的最后一天
    public static String getEndDayOfMonth(LocalDate date) {
        LocalDate now = date.with(TemporalAdjusters.lastDayOfMonth());
        Date d = Date.from(now.atStartOfDay(ZoneId.systemDefault()).plusDays(1L).minusNanos(1L).toInstant());
        return DateFormatUtil.format5(d);
    }
    
    public static void main(String[] args) {
    	System.out.println(otherDay(0));
	}
}
