/**
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.util;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * [时间工具类]
 *
 * @ProjectName: [gooday-jobs]
 * @Author: [zhenlongbiao]
 * @CreateDate: [2014/11/18 19:15]
 * @Update: [创建时间工具类] BY[zhenlongbiao][2014/11/18]
 * @Version: [v1.0]
 */
public class DateUtil {
    private static Logger logger = LoggerFactory.getLogger(DateUtil.class);

    public static final String DATE_YEAR_MONTH_FORMAT = "yyyyMM";

    public static final String MONTH_FORMAT = "yyyy-MM";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DATETIME_FORMAT2 = "yyyy/MM/dd HH:mm:ss";


    public static final String DATEFORMAT = "yyyyMMdd";

    public static final String DATEFORMAT_YYMMDD = "yyMMdd";


    public static final String TIME_FORMAT = "yyyyMMddHHmmss";

    public static final String TIME_FORMAT_YY = "yyMMddHHmmss";

    public DateUtil() {
    }

    public static String getTodayYYYYMMDD_HHMMSS() {
        return dateToString(new Date(), DATETIME_FORMAT);
    }

    public static String getTodayYYYYMMDD_HHMMSS2() {
        return dateToString(new Date(), DATETIME_FORMAT2);
    }

    public static String getTodayYYMMDD() {
        return dateToString(new Date(), DATEFORMAT_YYMMDD);
    }

    public static String getTodayYYYYMMDDHHMMSS() {
        return dateToString(new Date(), TIME_FORMAT);
    }

    public static String getTodayYYYYMMDD() {
        return dateToString(new Date(), DATEFORMAT);
    }

    public static String getTomorrowYYYYMMDD() {
        return dateToString(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000), DATEFORMAT);
    }

    public static Date stringToDate(String stringValue) {
        return stringToDate(stringValue, DATETIME_FORMAT);
    }

    public static Date strToDate(String stringValue) {
        return stringToDate(stringValue, DATE_FORMAT);
    }

    public static Date stringToDate(String stringValue, String format) {
        Date dateValue = null;
        if (stringValue != null) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(format);
                dateValue = dateFormat.parse(stringValue);

            } catch (ParseException ex) {
            }
        }
        return dateValue;
    }

    public static String dateToString(Date dateValue) {
        return dateToString(dateValue, DATETIME_FORMAT);
    }

    public static String dateToString(Date dateValue, String format) {
        if (dateValue == null) {
            return null;
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.format(dateValue);
        }
    }

    public static Date getDate() {
        Calendar c = Calendar.getInstance();
        return stringToDate(dateToString(c.getTime()));
    }

    public static Date getDate(String format) {
        Calendar c = Calendar.getInstance();
        return stringToDate(dateToString(c.getTime(), format));
    }

    public static String getToday() {
        return dateToString(new Date(), "yyyy-MM-dd");
    }

    public static String getToday(String format) {
        String dateStr = dateToString(new Date(), format);
        return dateStr;
    }

    /**
     * 获得当前年
     *
     * @return
     */
    public static String getNowYear() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        return String.valueOf(year);
    }

    /**
     * 获得当前月
     *
     * @return
     */
    public static String getNowMonth() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        if (month < 10) {
            return "0" + month;
        } else {
            return String.valueOf(month);
        }
    }

    /**
     * 获得当前日
     *
     * @return
     */
    public static String getNowDay() {
        return dateToString(new Date(), "dd");

    }

    /**
     * 得到两个日期之间的所有天数字符串
     *
     * @param beginDate
     * @param endDate
     * @return
     * @throws java.text.ParseException
     */
    public static String[] getTwoDateDiffer(String beginDate, String endDate)
            throws ParseException {

        SimpleDateFormat timeFormatter = new SimpleDateFormat(DATE_FORMAT);
        Date dateBegin = timeFormatter.parse(beginDate);
        Date dateEnd = timeFormatter.parse(endDate);
        long millionsecondsInOneDay = (24 * 3600 * 1000);
        long distinctionDays = (dateEnd.getTime() - dateBegin.getTime())
                / millionsecondsInOneDay + 1;

        String[] dateArray = new String[new Long(distinctionDays).intValue()];

        if (distinctionDays == 1) {
            dateArray[0] = timeFormatter.format(dateBegin);
        } else {
            for (int i = 0; i < distinctionDays; i++) {
                Date tempDate = new Date(dateBegin.getTime() + i
                        * millionsecondsInOneDay);
                dateArray[i] = timeFormatter.format(tempDate);
            }
        }
        return dateArray;

    }

    /**
     * 字符型日期转化 Thu Apr 29 08:57:29 CST 2004转换成2004-05-04 08:57:29
     *
     * @param stringdate
     * @return
     */
    public static String cSTDateTransDate(String stringdate) {
        StringBuffer resultDate = new StringBuffer();
        resultDate.append(stringdate.subSequence(0, 20)).append(
                stringdate.substring(24, 28));

        Date date = new Date(resultDate.toString());
        SimpleDateFormat sf = new SimpleDateFormat(DATETIME_FORMAT);

        return sf.format(date);
    }

    /**
     * 计算某日期与当前日期的相差天数
     */
    public static int dateTransToDayNum(String date) {
//        Date date1 = stringToDate(date, DATE_FORMAT);
//        int dayNum = date1.getDate() + date1.getMonth() * 30
//                + (date1.getYear() + 1900 - 2003) * 365;
//        int nowDayNum = getDate().getDate() + getDate().getMonth() * 30
//                + (getDate().getYear() + 1900 - 2003) * 365;
//
//        return (nowDayNum - dayNum);
        return 0;

    }

    /**
     * 获取大于或小于的日期天数
     * @param date
     * @param next
     * @return
     */
    public static Date getNextDate(Date date,int next){
        if(null==date)return null;
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH,next);
        return cal.getTime();
    }

    /**
     * 判断某个日期是否跟另一个日期一样
     */
    public static int dateCompareDate(Date oneDate, Date twodate) {

        SimpleDateFormat sf = new SimpleDateFormat(DATE_FORMAT);
        return (stringToDate(sf.format(oneDate), DATE_FORMAT))
                .compareTo(stringToDate(sf.format(twodate), DATE_FORMAT));
    }


    public static Date toBeginDate(String beginDate) {
        return toBeginDate(beginDate, DATETIME_FORMAT);
    }

    public static Date toEndDate(String endDate) {
        return toEndDate(endDate, DATETIME_FORMAT);
    }


    public static Date toBeginDate(String beginDate, String formater) {
        if (beginDate == null || beginDate.equals("")) {
            return null;
        }
        String d = beginDate + " 00:00:00";
        SimpleDateFormat sf = new SimpleDateFormat(formater);
        try {
            Date date = sf.parse(d);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static Date toEndDate(String endDate, String formater) {
        if (endDate == null || endDate.equals("")) {
            return null;
        }
        String d = endDate + " 23:59:59";
        SimpleDateFormat sf = new SimpleDateFormat(formater);
        try {
            Date date = sf.parse(d);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 取上个月第一天
     *
     * @return
     */
    public static String lastMonFirstDay() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String months = "";
        String days = "";
        if (month > 1) {
            month--;
        } else {
            year--;
            month = 12;
        }
        if (!(String.valueOf(month).length() > 1)) {
            months = "0" + month;
        } else {
            months = String.valueOf(month);
        }
        if (!(String.valueOf(day).length() > 1)) {
            days = "0" + day;
        } else {
            days = String.valueOf(day);
        }
        String firstDay = "" + year + "-" + months + "-01";
        String[] lastMonth = new String[2];
        lastMonth[0] = firstDay;
        return firstDay;
    }

    /**
     * 取上个月最后一天
     *
     * @return
     */
    public static String lastMonLastDay() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String months = "";
        String days = "";
        if (month > 1) {
            month--;
        } else {
            year--;
            month = 12;
        }
        if (!(String.valueOf(month).length() > 1)) {
            months = "0" + month;
        } else {
            months = String.valueOf(month);
        }
        if (!(String.valueOf(day).length() > 1)) {
            days = "0" + day;
        } else {
            days = String.valueOf(day);
        }
        String lastDay = "" + year + "-" + months + "-" + days;
        String[] lastMonth = new String[2];
        lastMonth[1] = lastDay;
        return lastDay;
    }
}
