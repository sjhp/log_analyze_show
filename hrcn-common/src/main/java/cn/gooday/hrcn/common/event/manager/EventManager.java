/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.event.manager;


import cn.gooday.hrcn.common.InstanceManager;
import cn.gooday.hrcn.common.config.ConfigService;
import cn.gooday.hrcn.common.constant.Constants;
import cn.gooday.hrcn.common.constant.EnumEventType;
import cn.gooday.hrcn.common.context.RuntimeContext;
import cn.gooday.hrcn.common.event.Event;
import cn.gooday.hrcn.common.event.handler.IEventHandler;
import cn.gooday.hrcn.common.exception.EventManagerException;
import cn.gooday.hrcn.common.exception.QueueManagerException;
import cn.gooday.hrcn.common.notify.INotify;
import cn.gooday.hrcn.common.queue.QueueManager;
import cn.gooday.hrcn.common.util.LogUtil;
import cn.gooday.hrcn.common.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * [简短描述该类的功能]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/2/11 0:30]
 * @Update: [说明本次修改内容] BY[tophawk][2015/2/11]
 * @Version: [v1.0]
 */
public class EventManager implements INotify {
    /**
     * 读取配置
     */
    private ConfigService configService = RuntimeContext.get(Constants.CONTEXT_CONFIGSERVICE);
    /**
     * 业务间隔时间 默认单位为ms 经测试若值小于100则无法捕获未catch异常的原因
     */
    private final int INTERVAL_TIME = configService.getInt("INTERVAL_TIME");
    /**
     * 重试次数
     */
    private final int REPETITIONS = configService.getInt("REPETITIONS");
    /**
     * 业务优先级
     */
    private final int PRIORITY = configService.getInt("PRIORITY");
    /**
     * 监听者序列
     */
    private static Map<EnumEventType, IEventHandler> listeners;
    /**
     * 事件队列
     */
    private QueueManager eQueueManager;


    /**
     * 初始化资源 并给定线程名称以便异常记录和查找
     *
     * @param queueManager
     */
    public EventManager(QueueManager queueManager) {
        this.eQueueManager = queueManager;
        init();
    }

    /**
     * 定时被回调
     *
     * @param key
     */
    @Override
    public void schedule(String key) {
        try {
            Event event = eQueueManager.pop();
            if (event != null) {
                if (!processCommand(event)) {
                    //重新入队
                    eQueueManager.put(event);
                }
            }
        } catch (QueueManagerException e) {
            //log记录
            LogUtil.err("after dispatch event enqueue failed｛｝", e.getMessage());
        }
    }

    public void dispatchEvent(Event event) {
        LogUtil.debug("dispatch event type {}", event.getEventType());
        //notify
        IEventHandler lst = listeners.get(event.getEventType());
        if (lst != null) {
            lst.handle(event);
        }
    }

    private void init() throws EventManagerException {
        //注册监听
        for (EnumEventType et : EnumEventType.values()) {
            String eventKey = et.toString();
            String className = configService.getString(eventKey);
            if (StringUtil.isBlank(className)) {
                register(et, null);
            } else {
                register(et, InstanceManager.createInstance(className));
            }
        }
    }

    /**
     * 注册监听者
     *
     * @param et
     * @param listener
     */
    public void register(EnumEventType et, IEventHandler listener) {
        //根据事件类型注册监听

        if (listeners == null) listeners = new HashMap<>();
        if (et == null && listener == null)
            throw new EventManagerException("invalid arguments!", null);
        if (listeners.get(et) == null)
            listeners.put(et, listener);
    }

    /**
     * [执行命令]
     *
     * @return 布尔
     */
    private boolean processCommand(Event event) {
        try {
            //执行事件
            dispatchEvent(event);
        } catch (RuntimeException e) {
            e.printStackTrace();
            LogUtil.err("dispatch event failed:{},message{}", event.getEventNo(), e.getMessage());
            throw new EventManagerException("run dispatch event failed", event);
        }
        return true;
    }

    /**
     * [获取业务的优先级]
     *
     * @return
     */
    @Override
    public int getPriorityValue() {
        return Thread.NORM_PRIORITY + PRIORITY;
    }

    /**
     * [获取业务间隔时间]
     *
     * @return
     */
    @Override
    public int getIntervalTime() {
        return INTERVAL_TIME;
    }
}
