/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.util;

import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Set;

/**
 * [简短描述该类的功能]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/2/14 20:55]
 * @Update: [说明本次修改内容] BY[tophawk][2015/2/14]
 * @Version: [v1.0]
 */
public class StringUtil {
    /**
     * 字符串连接，避免加号的滥用
     *
     * @param params
     * @return
     */
    public static String join(String... params) {
        StringBuffer buffer = new StringBuffer();
        for (String item : params) {
            buffer.append(item);
        }
        return buffer.toString();
    }

    /**
     * [转换为字符串
     * 如果给定的值为null，或者转换失败，返回默认值
     * 转换失败不会报错]
     *
     * @param value 被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static String toStr(Object value, String defaultValue) {
        if(null == value) {
            return defaultValue;
        }
        if(value instanceof String) {
            return (String)value;
        }
        return value.toString();
    }

    /**
     * [转换为int
     * 如果给定的值为空，或者转换失败，返回默认值
     * 转换失败不会报错]
     *
     * @param value 被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Integer toInt(Object value, Integer defaultValue) {
        if (value == null) return defaultValue;
        if(value instanceof Integer) {
            return (Integer)value;
        }
        final String valueStr = value.toString();
        if (StringUtil.isBlank(valueStr)) return defaultValue;
        try {
            return Integer.parseInt(valueStr);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * [转换为long
     * 如果给定的值为空，或者转换失败，返回默认值
     * 转换失败不会报错]
     *
     * @param value 被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Long toLong(Object value, Long defaultValue) {
        if (value == null) return defaultValue;
        if(value instanceof Long) {
            return (Long)value;
        }
        final String valueStr = value.toString();
        if (StringUtil.isBlank(valueStr)) return defaultValue;
        try {
            return new BigDecimal(valueStr).longValue();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * [转换为double
     * 如果给定的值为空，或者转换失败，返回默认值
     * 转换失败不会报错]
     *
     * @param value 被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Double toDouble(Object value, Double defaultValue) {
        if (value == null) return defaultValue;
        if(value instanceof Double) {
            return (Double)value;
        }
        final String valueStr = value.toString();
        if (StringUtil.isBlank(valueStr)) return defaultValue;
        try {
            return new BigDecimal(valueStr).doubleValue();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * [转换为Float
     * 如果给定的值为空，或者转换失败，返回默认值
     * 转换失败不会报错]
     *
     * @param value 被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Float toFloat(Object value, Float defaultValue) {
        if (value == null) return defaultValue;
        if(value instanceof Float) {
            return (Float)value;
        }
        final String valueStr = value.toString();
        if (StringUtil.isBlank(valueStr)) return defaultValue;
        try {
            return Float.parseFloat(valueStr);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * [转换为boolean
     * 如果给定的值为空，或者转换失败，返回默认值
     * 转换失败不会报错]
     *
     * @param value 被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Boolean toBool(Object value, Boolean defaultValue) {
        if (value == null) return defaultValue;
        if(value instanceof Boolean) {
            return (Boolean)value;
        }
        final String valueStr = value.toString();
        if (StringUtil.isBlank(valueStr)) return defaultValue;
        try {
            return Boolean.parseBoolean(valueStr);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * [半角转全角]
     *
     * @param input String.
     * @return 全角字符串.
     */
    public static String toSBC(String input) {
        return toSBC(input, null);
    }

    /**
     * [半角转全角]
     *
     * @param input String
     * @param notConvertSet 不替换的字符集合
     * @return 全角字符串.
     */
    public static String toSBC(String input, Set<Character> notConvertSet) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if(null != notConvertSet && notConvertSet.contains(c[i])) {
                //跳过不替换的字符
                continue;
            }

            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);

            }
        }
        return new String(c);
    }

    /**
     * [全角转半角]
     *
     * @param input String.
     * @return 半角字符串
     */
    public static String toDBC(String input) {
        return toDBC(input, null);
    }

    /**
     * [替换全角为半角]
     * @param text 文本
     * @param notConvertSet 不替换的字符集合
     * @return 替换后的字符
     */
    public static String toDBC(String text, Set<Character> notConvertSet) {
        char c[] = text.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if(null != notConvertSet && notConvertSet.contains(c[i])) {
                //跳过不替换的字符
                continue;
            }

            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);
            }
        }
        String returnString = new String(c);

        return returnString;
    }

    /**
     * [字符串是否为空白 空白的定义如下
     * 1、为null
     * 2、为不可见字符（如空格）
     * 3、""]
     *
     * @param str 被检测的字符串
     * @return 是否为空
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }
}
