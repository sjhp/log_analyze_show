/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * [封装slf4j为基础的通用方法]
 *
 * @ProjectName: [seller-platform]
 * @Author: [tophawk]
 * @CreateDate: [2014/12/6 16:28]
 * @Update: [说明本次修改内容] BY[tophawk][2014/12/6]
 * @Version: [v1.0]
 */
public class LogUtil {
    private static final Logger logger = LoggerFactory.getLogger("HRCN");

    private static final int LEVEL_INFO = 2;
    private static final int LEVEL_WARN = 3;
    private static final int LEVEL_ERROR = 4;
    private static final int LEVEL_DEBUG = 1;

    private static final String PACKAGE_FLAG = "cn.gooday";

    /**
     * 别new本类，直接调用静态方法即可
     */
    private LogUtil() {
    }

    /**
     * 打印调试信息，可以传两个参数，调用示例见本文件main方法
     *
     * @param content
     * @param params
     */
    public static void debug(String content, Object... params) {
        messageUp(LEVEL_DEBUG, content, params);
    }

    /**
     * 打印提示信息，可以传两个参数，调用示例见本文件main方法
     *
     * @param content
     * @param params
     */
    public static void info(String content, Object... params) {
        messageUp(LEVEL_INFO, content, params);
    }

    /**
     * 打印警告信息，可以传两个参数，调用示例见本文件main方法
     *
     * @param content
     * @param params
     */
    public static void warn(String content, Object... params) {
        messageUp(LEVEL_WARN, content, params);
    }

    /**
     * 打印错误信息，可以传两个参数，调用示例见本文件main方法
     *
     * @param content
     * @param params
     */
    public static void err(String content, Object... params) {
        messageUp(LEVEL_ERROR, content, params);
    }

    public static void sendErr(String content, Object... params) {
        messageUp(LEVEL_ERROR, content, params);
    }

    public static void sendErr(Exception e) {
        //e.printStackTrace();
        err("异常:{}", getCauseMsg(e));
    }

    /**
     * 可以将exception导向这里,多态
     *
     * @param e
     */
    public static void err(Exception e) {
        //e.printStackTrace();
        err("异常:{}", getCauseMsg(e));
    }

    /**
     * 可以将exception导向这里,多态
     *
     * @param e
     */
    public static void err(String content, Exception e) {
        err(content.concat("{}"), getCauseMsg(e));
    }

    /**
     * 可以将exception导向这里,多态
     * 用法：LogUtil.err("该ID{}出现错误:",e,entityId1);
     *
     * @param e
     */
    public static void err(String content, Object param, Exception e) {
        content = content.replace("{}", param.toString());
        err(content, e);
    }

    /**
     * 简单打印方法
     * @param level
     * @param content
     * @param params
     */
    /*
    private static void message(Integer level,String content,Object... params){
        StringBuffer bufContent = new StringBuffer();
        bufContent.append(getStackMessage());
        bufContent.append(content);
        switch(level){
            case LEVEL_INFO:
                logger.info(bufContent.toString(), params);
                break;
            case LEVEL_WARN:
                logger.warn(bufContent.toString(), params);
                break;
            case LEVEL_ERROR:
                logger.error(bufContent.toString(), params);
                break;
            default:
                logger.debug(bufContent.toString(),params);
        }
    }*/

    /**
     * 返回调用信息串,deprecated
     * @return
     */
    /*
    private static String getStackMessage() {
        StackTraceElement stack[] = Thread.currentThread().getStackTrace();
        for (StackTraceElement ste : stack) {
            if ((ste.getClassName().indexOf("LogUtil")) == -1 && ste.getClassName().indexOf("Thread") == -1) {
                String fileName = ste.getFileName();
                if(fileName !=null) fileName = fileName.replace(".java","->");

                //建立返回串
                StringBuffer buf = new StringBuffer();
                buf.append(fileName);
                buf.append(ste.getMethodName()).append("(").append(ste.getLineNumber()).append(")").append("：");
                return buf.toString();
            }
        }
        return "";
    }*/

    /**
     * 复杂打印方法
     *
     * @param level
     * @param content
     * @param params
     */
    private static void messageUp(Integer level, String content, Object... params) {
        StringBuffer bufContent = new StringBuffer();

        //获取调用信息
        Map<String, String> retMap = getMapStackMessage();
        //进行logger筛选
        Logger curLogger = logger;

        bufContent.append(retMap.get("content"));
        bufContent.append(content);
        switch (level) {
            case LEVEL_DEBUG:
                if (curLogger.isDebugEnabled()) {
                    curLogger.debug(bufContent.toString(), params);
                }
                break;
            case LEVEL_WARN:
                if (curLogger.isWarnEnabled()) {
                    curLogger.warn(bufContent.toString(), params);
                }
                break;
            case LEVEL_ERROR:
                curLogger.error(bufContent.toString(), params);
                break;
            default:
                if (curLogger.isInfoEnabled()) {
                    curLogger.info(bufContent.toString(), params);
                }
        }
    }

    /**
     * 返回调用信息，类信息，包含注解，类名，格式化信息
     *
     * @return
     */
    private static Map<String, String> getMapStackMessage() {
        Map<String, String> retMap = new HashMap<>();

        StackTraceElement stack[] = Thread.currentThread().getStackTrace();
        for (StackTraceElement ste : stack) {
            if ((ste.getClassName().indexOf("LogUtil")) == -1 && ste.getClassName().indexOf("Thread") == -1) {
                String className = ste.getClassName();
                String fileName = ste.getFileName();
                if (fileName != null) fileName = fileName.replace(".java", "->");

                /*
                //see if in action或者service
                try {
                    //查找最后一个调用者的声明，先按根据注解，在Action和Service维度区分，以后可扩展
                    Class actionClass = Class.forName(className);

                    if(actionClass.getName().contains("cn.gooday.common")){//common底下放入service范围
                        retMap.put("service","1");
                    }

                    for(Annotation annotation:actionClass.getAnnotations()){
                        if(annotation.toString().contains("Service")){
                            retMap.put("service","1");
                        }else if(annotation.toString().contains("Action")){
                            retMap.put("action","1");
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                */
                //see if in action或者service，从约定性的路径

                //建立返回串
                StringBuffer buf = new StringBuffer();

                //模块

                buf.append(fileName);
                buf.append(ste.getMethodName()).append("(").append(ste.getLineNumber()).append(")").append("：");

                retMap.put("class", className);
                retMap.put("content", buf.toString());
                return retMap;
            }
        }
        return retMap;
    }

    /**
     * 获取，过滤并格式化异常信息
     *
     * @param e
     * @return
     */
    private static String getCauseMsg(Exception e) {
        StringBuffer stackBuf = new StringBuffer();
        stackBuf.append(e.toString());
        String stacks = Arrays.asList(e.getStackTrace()).stream().map(el -> {
            StringBuffer buf = new StringBuffer();
            buf.append(el.getClassName()).append(".").append(el.getMethodName()).append("(").append(el.getLineNumber()).append(")");
            return buf.toString();
        }).collect(Collectors.joining(" \r\nat "));
        if (StringUtil.isBlank(stacks)) {
            stackBuf.append(" \r\nat ");
            stackBuf.append(stacks);
        }
        return stackBuf.toString();
    }

    /**
     * 返回当前是否充许debug
     *
     * @return
     */
    public static boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    /**
     * 调用示例
     *
     * @param args
     */
    public static void main(String[] args) throws UnsupportedEncodingException {
        LogUtil.info("hello {},{}", "1", "2", "3");
        LogUtil.info("hello {},i am {}", 1333, "mike");
        LogUtil.info("hello {},i am {}", "美女", "英雄");
        LogUtil.info("hello 你最棒！");

        //异常,几种重载
        String orderId = "P1234567";
        Exception ex = new RuntimeException("这是一个异常！");
        LogUtil.err(ex);
        LogUtil.err("处理支付时发生异常：", ex);
        LogUtil.err("下单异常 id={}", orderId, ex);

        double pricePerProduct = new BigDecimal(Double.parseDouble("999999.00") * 1).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        LogUtil.debug("price:" + pricePerProduct);
    }
}
