package com.train.common.util;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by JoinIn
 * DATE: 4/19/22
 **/

public class DateUtils {
    private final static DateTimeFormatter yearMonthFormatStr = DateTimeFormatter.ofPattern("yyyyMM");
    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final static DateTimeFormatter monthCNFormatter = DateTimeFormatter.ofPattern("yyyy年M月");
    private final static DateTimeFormatter dateCNFormatter = DateTimeFormatter.ofPattern("yyyy年M月d日");


    /**
     * date 2 localdatetime
     *
     * @param date
     * @return
     */
    public static LocalDateTime date2LocalDateTime(Date date) {
        Instant startTime = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(startTime, zoneId);
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static Date getNow() {
        return new Date(System.currentTimeMillis());
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static LocalDate getLocalDateNow() {
        return LocalDate.now();
    }

    /**
     * 日期转换成yyyyMM
     *
     * @param dateTime
     * @return 202201
     */
    public static String getYearMonthFormat(LocalDateTime dateTime) {
        return getYearMonthFormat(dateTime.toLocalDate());
    }

    /**
     * 日期转换成yyyyMM
     *
     * @param date
     * @return 202201
     */
    public static String getYearMonthFormat(LocalDate date) {
        return date.format(yearMonthFormatStr);
    }

    /**
     * 获取年份
     *
     * @param dateTime
     * @return 2022
     */
    public static int getYear(LocalDateTime dateTime) {
        return getYear(dateTime.toLocalDate());
    }

    /**
     * 获取年份
     *
     * @param date
     * @return 2022
     */
    public static int getYear(LocalDate date) {
        return date.getYear();
    }

    /**
     * 获取月份
     *
     * @param dateTime
     * @return 1
     */
    public static int getMonth(LocalDateTime dateTime) {
        return getMonth(dateTime.toLocalDate());
    }

    /**
     * 获取月份
     *
     * @param date
     * @return 1
     */
    public static int getMonth(LocalDate date) {
        return date.getMonthValue();
    }

    /**
     * 获取日期
     *
     * @param dateTime
     * @return 1
     */
    public static int getDayOfMonth(LocalDateTime dateTime) {
        return getMonth(dateTime.toLocalDate());
    }

    /**
     * 获取日期
     *
     * @param date
     * @return 1
     */
    public static int getDayOfMonth(LocalDate date) {
        return date.getDayOfMonth();
    }

    /**
     * 日期类型转String
     *
     * @param dateTime
     * @return 2022-01-01 11:21:21
     */
    public static String getDatetimeStr(LocalDateTime dateTime) {
        return dateTime.format(dateTimeFormatter);
    }

    /**
     * 日期类型转String
     *
     * @param dateTime
     * @return 2022-03-11
     */
    public static String getDateStr(LocalDateTime dateTime) {
        return getDateStr(dateTime.toLocalDate());
    }

    /**
     * 日期类型转String
     *
     * @param date
     * @return 2022-03-11
     */
    public static String getDateStr(LocalDate date) {
        return date.format(dateFormatter);
    }

    /**
     * String 转 日期yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static LocalDate parseStrToLocalDate(String date) {
        return LocalDate.parse(date, dateFormatter);
    }

    /**
     * String 转 日期yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static LocalDate parseStrToLocalDatetime(String date) {
        return LocalDate.parse(date, dateTimeFormatter);
    }

    /**
     * 日期加减天数
     *
     * @param date
     * @param days
     * @return
     */
    public static LocalDate plusDays(LocalDate date, long days) {
        return date.plusDays(days);
    }

    /**
     * 日期加减天数
     *
     * @param date
     * @param days
     * @return
     */
    public static LocalDate plusDays(String date, long days) {
        return parseStrToLocalDate(date).plusDays(days);
    }


    /**
     * String转日期
     *
     * @param datetime
     * @return
     */
    public static LocalDateTime parseToLocalDatetime(String datetime) {
        return LocalDateTime.parse(datetime, dateTimeFormatter);
    }

    /**
     * 日期转中文日期 2022年1月日
     *
     * @param localDateTime
     * @return
     */
    public static String getDateCNStr(LocalDateTime localDateTime) {
        return getDateCNStr(localDateTime.toLocalDate());
    }

    /**
     * 日期转中文日期 2022年1月1日
     *
     * @param localDate
     * @return
     */
    public static String getDateCNStr(LocalDate localDate) {
        return localDate.format(dateCNFormatter);
    }

    /**
     * 日期转中文日期 2022年1月日
     *
     * @param localDateTime
     * @return
     */
    public static String getMonthCNStr(LocalDateTime localDateTime) {
        return getMonthCNStr(localDateTime.toLocalDate());
    }

    /**
     * 日期转中文日期 2022年1月1日
     *
     * @param localDate
     * @return
     */
    public static String getMonthCNStr(LocalDate localDate) {
        return localDate.format(monthCNFormatter);
    }

    /**
     * 获取日期时间戳
     *
     * @param date
     * @return
     */
    public static long getTimestamp(LocalDate date) {
        return date.atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /**
     * 计算天数差
     *
     * @param start
     * @param end
     * @return
     */
    public static long diffDays(LocalDateTime start, LocalDateTime end) {
        return Duration.between(start, end).toDays();
    }

    /**
     * 计算天数差
     *
     * @param start
     * @param end
     * @return
     */
    public static long diffDays(LocalDate start, LocalDate end) {
        return Duration.between(start.atStartOfDay(), end.atStartOfDay()).toDays();
    }

    /**
     * 比较并返回大的日期
     *
     * @param source
     * @param target
     * @return
     */
    public static LocalDate maxDate(LocalDate source, LocalDate target) {
        return source.compareTo(target) >= 0 ? source : target;
    }

    /**
     * 获取某年第一天日期
     *
     * @param year 年份
     * @return Date
     */
    public static Date getFirstOfYear(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

    /**
     * 获取某年最后一天日期
     *
     * @param year 年份
     * @return Date
     */
    public static Date getLastOfYear(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        return calendar.getTime();
    }

    /**
     * 日期转string
     *
     * @param date
     * @return
     */
    public static String parseDateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    /**
     * 根据时间获取本年第一天
     *
     * @param date
     * @return
     */
    public static String getFirstDateByDate(String date) {
        LocalDate localDate = parseStrToLocalDate(date);
        int year = localDate.getYear();
        Date firstOfYear = getFirstOfYear(year);
        return parseDateToString(firstOfYear);
    }

    /**
     * 根据传入的时间，获取上个月的最后一天
     *
     * @param date
     * @return
     */
    public static LocalDate lastDayOfLastMonth(String date) {
        LocalDate localDate = parseStrToLocalDate(date);
        return localDate.minusMonths(1L).with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 根据月份和年份 获取当月最后一天
     *
     * @param year
     * @param month
     * @return
     */
    public static LocalDate lastDayOfMonth(int year, int month) {
        LocalDate localDate = LocalDate.of(year, month, 1);
        return localDate.with(TemporalAdjusters.lastDayOfMonth());
    }

}
