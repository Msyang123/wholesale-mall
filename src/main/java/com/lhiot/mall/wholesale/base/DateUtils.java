package com.lhiot.mall.wholesale.base;
import java.text.SimpleDateFormat;
import java.util.Calendar;
/**
 * @author zhangs on 2018/5/5.
 */
public class DateUtils{
    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    //上月第一天
    public static String perMonthFirstDay(){
        String firstDay;
        Calendar cal_1=Calendar.getInstance();//获取当前日期
        cal_1.add(Calendar.MONTH, -1);
        cal_1.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
        firstDay = format.format(cal_1.getTime());
        return firstDay;
    }
    //本月第一天
    public static String thisMonthFirstDay(){
        String firstDay;
        Calendar cal_1=Calendar.getInstance();//获取当前日期
        cal_1.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
        firstDay = format.format(cal_1.getTime());
        return firstDay;
    }
    //下月第一天
    public static String nextMonthFirstDay(){
        String firstDay;
        Calendar cal_1=Calendar.getInstance();//获取当前日期
        cal_1.add(Calendar.MONTH, 1);
        cal_1.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
        firstDay = format.format(cal_1.getTime());
        return firstDay;
    }


}
