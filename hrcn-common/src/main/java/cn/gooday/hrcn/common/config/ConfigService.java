/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.config;

import cn.gooday.hrcn.common.util.LogUtil;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [配置信息管理]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/2/14 22:32]
 * @Update: [说明本次修改内容] BY[tophawk][2015/2/14]
 * @Version: [v1.0]
 */
public class ConfigService {
    private final String LOCAL_CONFIG_FILE = "config.properties";

    /**
     * 存储本地的和远程的配置信息
     */
    private Map<String, String> PROPS = new ConcurrentHashMap<>();

    public ConfigService() {
    }

    /**
     * @return
     */
    public boolean init() {
        try {
            loadLocalProps(LOCAL_CONFIG_FILE);
        } catch (Exception e) {
            LogUtil.err("Load {} fail:", LOCAL_CONFIG_FILE, e);
            return false;
        }
        return true;
    }

    /**
     * 加载属性文件
     */
    public void loadLocalProps(String propsPath) {
        Properties props = new Properties();
        if (propsPath == null || "".equals(propsPath)) {
            throw new IllegalArgumentException();
        }
        //load
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(propsPath)) {
            props.load(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Set<Map.Entry<Object, Object>> entries = props.entrySet();
        for (Map.Entry entry : entries) {
            PROPS.put(entry.getKey().toString(), entry.getValue().toString());
        }
    }

    /**
     * 远程配置信息装载
     */
    private void loadRemoteProps() {

    }

    /**
     * 获取字符型属性
     */
    public String getString(String key) {
        assertNull();
        return PROPS.getOrDefault(key, "").toString();
    }

    /**
     * 获取数值型属性
     */
    public int getInt(String key) {
        assertNull();
        try {
            return Integer.valueOf(PROPS.getOrDefault(key, "0"));
        } catch (RuntimeException e) {
            LogUtil.err("配置项{}非数字：", key, e);
        }
        return 0;
    }

    /**
     * 获取数值型属性
     */
    public long getLong(String key) {
        assertNull();
        if (PROPS.containsKey(key)) {
            try {
                return Long.valueOf(PROPS.getOrDefault(key, "0"));
            } catch (RuntimeException e) {
                LogUtil.err("配置项{}非数字：", key, e);
            }
        }
        return 0;
    }

    private void assertNull() {
        if (PROPS == null) throw new RuntimeException("请先初始化配置ConfigUtil.init()！");
    }
}
