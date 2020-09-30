package com.tincery.gaea.core.base.tool.util;


import com.tincery.gaea.core.base.exception.AnalysisException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/***
 *
 * @author gxz
 * @date 2019/12/10
 **/
public class DateUtils {
    private DateUtils() {
        throw new RuntimeException("can't new instance");
    }

    public final static ZoneId DEFAULT_ZONE = ZoneId.systemDefault();

    public final static Map<String, DateTimeFormatter> FORMATTER_MAP = new HashMap<>();
    public final static DateTimeFormatter DEFAULT_DATE_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter PATTERN_YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final DateTimeFormatter CN_DATE = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
    public final static DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
    public final static DateTimeFormatter LINK_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmm");


    /***如果long值超过了这个值一定是微秒 */
    private static final long MICROSECOND = 1000000000000000L;

    /***公司创建时间 如果毫秒值低过了这个数字 那说明这个数字是错误的*/
    private static final long CREATE_COMPANY = 1420041600000L;



    public final static int SECOND = 1000;
    public final static int MINUTE = 60 * SECOND;
    public final static int HOUR = 60 * MINUTE;
    public final static long DAY = 24L * HOUR;
    public final static long WEEK = 7 * DAY;


    static{
        FORMATTER_MAP.put("yyyy-MM-dd HH:mm:ss",DEFAULT_DATE_PATTERN);
        FORMATTER_MAP.put("yyyyMMdd",PATTERN_YYYYMMDD);
        FORMATTER_MAP.put("yyyy年MM月dd日",CN_DATE);
        FORMATTER_MAP.put("yyyy-MM-dd HH",HOUR_FORMATTER);
        FORMATTER_MAP.put("yyyyMMddHHmm", LINK_FORMATTER);

    }



    /**
     * 获取当前时间字符串
     */
    public static String now() {
        return LocalDateTime.now().format(DEFAULT_DATE_PATTERN);
    }

    /**
     * 返回两个时间中靠后的时间
     * @param time1 第一个时间
     * @param time2 第二个时间
     * @param big   true返回靠后的  false 返回靠前的
     */
    public static LocalDateTime swap(LocalDateTime time1, LocalDateTime time2, boolean big) {
        if (time1 == null) {
            return time2;
        }
        if (time2 == null) {
            return time1;
        }
        int i = time1.compareTo(time2);
        if (i > 0) {
            return big ? time1 : time2;
        } else {
            return big ? time2 : time1;
        }
    }

    public static LocalDateTime swap(LocalDateTime time1, LocalDateTime time2) {
        return swap(time1, time2, true);
    }

    public static String format(long timeStamp, String formatRex) {
        DateTimeFormatter dateTimeFormatter = FORMATTER_MAP.getOrDefault(formatRex, DateTimeFormatter.ofPattern(formatRex));
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp), DEFAULT_ZONE).format(dateTimeFormatter);
    }

    public static String format(long timeStamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp), DEFAULT_ZONE).format(DEFAULT_DATE_PATTERN);
    }


    public static long duration(long startTime) {
        return Instant.now().toEpochMilli() - startTime;
    }

    /****
     * 校验时间
     * 如果是微秒 会被转换成毫秒 如果是不符合范围的时间 将会报错
     * @author gxz
     * @param time 时间戳
     * @return long
     **/
    public static long validateTime(long time){
        if (time > MICROSECOND) {
            time /= 1000;
        }
        if (time < CREATE_COMPANY || time > (System.currentTimeMillis() + DAY)) {
            throw new AnalysisException();
        }
        return time;
    }

    public static LocalDateTime Date2LocalDateTime(Date date){
        Instant instant = date.toInstant();
        return LocalDateTime.ofInstant(instant,ZoneId.systemDefault());
    }
    public static long LocalDateTime2Long(LocalDateTime localDateTime){
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

}
