/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.client.bootstrap;

import cn.gooday.hrcn.collect.factory.CollectorFactory;
import cn.gooday.hrcn.collect.impl.DefaultDataCollector;
import cn.gooday.hrcn.collect.thread.CollectThread;
import cn.gooday.hrcn.common.InstanceManager;
import cn.gooday.hrcn.common.cache.ICache;
import cn.gooday.hrcn.common.cache.service.CacheService;
import cn.gooday.hrcn.common.config.ConfigService;
import cn.gooday.hrcn.common.constant.Constants;
import cn.gooday.hrcn.common.context.RuntimeContext;
import cn.gooday.hrcn.common.event.manager.AccidentHandleManager;
import cn.gooday.hrcn.common.event.manager.EventManager;
import cn.gooday.hrcn.common.queue.QueueManager;
import cn.gooday.hrcn.common.schedule.ScheduledExcutor;
import cn.gooday.hrcn.common.util.LogUtil;
import cn.gooday.hrcn.common.util.StatUtil;
import cn.gooday.hrcn.common.util.StringUtil;

import java.util.concurrent.Executors;

/**
 * [简短描述该类的功能]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/2/14 21:49]
 * @Update: [说明本次修改内容] BY[tophawk][2015/2/14]
 * @Version: [v1.0]
 */
public class ClientBootStrap {
    ScheduledExcutor excutor;

    public boolean init() {
        try {
            //初始化config
            ConfigService configService = new ConfigService();
            if (!configService.init()) {
                LogUtil.err("Config init fail!");
                return false;
            }
            //放入context
            RuntimeContext.set(Constants.CONTEXT_CONFIGSERVICE, configService);
            //RuntimeContext.setConfigService(configService);

            //初始化cache
            String cacheClazz = configService.getString(Constants.CLASS_CACHE_IMPL_KEY);
            ICache cache = InstanceManager.createInstance(cacheClazz);
            CacheService cacheService = new CacheService(cache);
            RuntimeContext.set(Constants.CONTEXT_CACHESERVICE, cacheService);

            //初始化QM
            QueueManager queueManager = new QueueManager();
            RuntimeContext.set(Constants.CONTEXT_QUEUEMANAGER, queueManager);

            //初始化EM
            EventManager eventManager = new EventManager(queueManager);

            //初始化Collector
            RuntimeContext.set(Constants.CONTEXT_COLLECTOR, new DefaultDataCollector());

            excutor = new ScheduledExcutor();
            excutor.register(eventManager);
            //错误事件处理
            AccidentHandleManager accidentHandleManager = new AccidentHandleManager();
            excutor.register(accidentHandleManager);

            //放入运行标志
            RuntimeContext.set(Constants.CONTEXT_ISSERVER,false);

            //客户端运行统计
            StatUtil.init();

            return true;
        } catch (Exception ex) {
            LogUtil.err(ex);
            return false;
        }
    }

    /**
     * 作为app调用的时候，启动
     */
    public void start() {
        //初始化collectors 并启动
        try {
            CollectThread thread = new CollectorFactory().create("cn.gooday.hrcn.collect.thread.FileLogCollectThread");
            Executors.newSingleThreadExecutor().submit(thread);
            excutor.scheduledBusiness();
        } catch (Exception ex) {
            LogUtil.err(ex);
            throw new RuntimeException(StringUtil.join("Start failed due to:", ex.getMessage()));
        }
    }
}
