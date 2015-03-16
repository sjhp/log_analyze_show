/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common;

import cn.gooday.hrcn.common.util.LogUtil;

/**
 * [创建对象使用]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/2/14 21:43]
 * @Update: [说明本次修改内容] BY[tophawk][2015/2/14]
 * @Version: [v1.0]
 */
public class InstanceManager {
    public static <T> T createInstance(String implClassName) {
        // 通过反射创建该实现类对应的实例
        T instance;
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Class<?> commandClass = Class.forName(implClassName,true,loader);
            instance = (T) commandClass.newInstance();
        } catch (Exception e) {
            LogUtil.err("Error in create instance:", e);
            throw new RuntimeException(e);
        }
        return instance;
    }
}
