/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.context;

import cn.gooday.hrcn.common.cache.service.CacheService;
import cn.gooday.hrcn.common.collect.ICollector;
import cn.gooday.hrcn.common.config.ConfigService;
import cn.gooday.hrcn.common.event.manager.EventManager;
import cn.gooday.hrcn.common.util.LogUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * [存储运行时通用数据，可以自定义放置一些运行时全局参数]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/2/15 10:11]
 * @Update: [说明本次修改内容] BY[tophawk][2015/2/15]
 * @Version: [v1.0]
 */
public class RuntimeContext {
    /**
     * 数据存储
     */
    private static final Map<Integer, Object> CONTEXT_DATA = new HashMap<>();

    private RuntimeContext() {
    }

    /**
     * 通用取数据方法
     *
     * @param key
     * @param <T>
     * @return
     */
    public static <T> T get(int key) {
        try {
            return (T) CONTEXT_DATA.get(key);
        } catch (Exception ex) {
            ex.printStackTrace();
            LogUtil.err(ex);
        }
        return null;
    }

    public static <T> void set(int key, T obj) {
        CONTEXT_DATA.put(key, obj);
    }
  }
