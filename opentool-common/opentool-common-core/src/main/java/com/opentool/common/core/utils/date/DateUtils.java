package com.opentool.common.core.utils.date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

/**
 * 日期处理工具类
 * / @Author: ZenSheep
 * / @Date: 2023/7/20 23:08
 */
public class DateUtils {
    // 如2019-03-22
    public static String YYY_MM_DD = "yyyy-MM-dd";
    // 如2019-03-22 09:11:52
    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    // 如2019-03-22T09:11:52.000+0000
    public static String YYYY_MM_DD_T_HH_MM_SS_SSS_ZZZZ = "yyyy-MM-dd'T'hh:mm:ss.SSSZ";
    // 如2016-09-03T00:00:00.000+08:00
    public static String YYYY_MM_DD_T_HH_MM_SS_SSS_ZZ_ZZ = "EEE MMM dd HH:mm:ss Z yyyy";

    // YYYY_MM_DD_T_HH_MM_SS_SSSZ 转 YYYY_MM_DD_HH_MM_SS
    public static String DateParseTime(Date date) {
        if (date == null) {
            return null;
        }

        DateFormat df = new SimpleDateFormat(YYYY_MM_DD_T_HH_MM_SS_SSS_ZZ_ZZ, Locale.UK);
        DateFormat df2 = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);

        Date date1 = null;
        try {
            date1 = df.parse(date.toString());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return df2.format(date1);
    }

    // YYYY_MM_DD_HH_MM_SS 转 YYYY_MM_DD_T_HH_MM_SS_SSSZ
    public static Date TimeParseDate(String time) {
        if (StringUtils.isEmpty(time)) {
            return null;
        }

        DateFormat df = new SimpleDateFormat(YYYY_MM_DD_T_HH_MM_SS_SSS_ZZ_ZZ, Locale.UK);
        DateFormat df2 = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);

        Date date1 =null;
        try {
            date1 = df2.parse(time);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Date date2 = null;
        try {
            date2 = df.parse(df.format(date1));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return date2;
    }

    /**
     * 将日期的斜杠转横杠
     * @param dateStr 日期字符串
     * @return 如2019-03-22转2019/03/22
     */
    public static String barConvertSlash(String dateStr) {
        return dateStr.replace('-','/');
    }

    /**
     * 日期路径
     * @param format 日期格式，如yyyy-MM-dd
     * @return 如2019-03-22
     */
    public static String getDatePath(String format) {
        Date now = new Date();
        return DateFormatUtils.format(now, format).replace(':', '-');
    }

    /**
     * 纳秒转换成mm:ss.SSS时间戳格式
     * @param nanoTimestamp
     * @return
     */
    public static String convertNanoTimestamp(BigInteger nanoTimestamp) {
        // 将纳秒级时间戳转换为秒级时间戳
        long seconds = nanoTimestamp.longValue() / 1_000_000_000;
        // 获取纳秒部分
        int nanos = (int) (nanoTimestamp.longValue() % 1_000_000_000);

        // 将秒级时间戳转换为LocalDateTime对象
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(seconds, nanos, ZoneOffset.UTC);

        // 定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm:ss.SSS");

        // 格式化时间并返回
        return dateTime.format(formatter);
    }
}
