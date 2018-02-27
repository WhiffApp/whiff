package com.app.whiff.whiff.NonRootScanner.Common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class JDateTimeTransform {
    public static final int SECOND = 60;
    public static final int HOUR = 3600;
    public static final int DAY = 86400;
    public static final int WEEK = 604800;


    Calendar currentTime;

    public JDateTimeTransform(){
        currentTime=new GregorianCalendar();
        currentTime.setTime(new Date());
    }
    public JDateTimeTransform(long timestamp){
        currentTime=new GregorianCalendar();
        currentTime.setTime(new Date(timestamp*1000));
    }
    public JDateTimeTransform(int year, int month, int day){
        currentTime=new GregorianCalendar(year,month,day);
    }

    public int getDay(){
        return currentTime.get(Calendar.DATE);
    }
    public int getMonth(){
        return currentTime.get(Calendar.MONTH)+1;
    }
    public int getYear(){
        return currentTime.get(Calendar.YEAR);
    }
    public long getTimestamp(){
        return currentTime.getTime().getTime()/1000;
    }

    public String toString(String formatString){
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        String date = format.format(currentTime.getTime());
        return date;
    }

    public JDateTimeTransform parse(String formatString,String content){
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        try {
            currentTime.setTime(format.parse(content));
            return this;
        } catch (ParseException e) {
            return null;
        }
    }

    public interface DateFormat{
        public String format(JDateTimeTransform date, long delta);
    }
    public String toString(DateFormat format){
        long delta = (System.currentTimeMillis() - currentTime.getTime().getTime())/1000;
        return format.format(this,delta);
    }
}