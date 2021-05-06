package com.example.study.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TimeUtils {

    public static String timeStampToDate(String timeStamp){
        // 把 String 类型的时间戳转换为格式化的日期 String
        SimpleDateFormat s= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return s.format(Long.valueOf(timeStamp));
    }


    public static Timestamp dateToTimeStamp(String date) throws ParseException {
        // 把格式化的日期 String 转换为 TimeStamp 类型的时间戳
        SimpleDateFormat s= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Long t;
        t = s.parse(date).getTime();
        return new Timestamp(t);
    }
}
