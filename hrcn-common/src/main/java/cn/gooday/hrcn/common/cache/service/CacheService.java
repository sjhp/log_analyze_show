/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.cache.service;

import cn.gooday.hrcn.common.bean.BaseBean;
import cn.gooday.hrcn.common.cache.ICache;
import cn.gooday.hrcn.common.constant.EnumEventType;
import cn.gooday.hrcn.common.event.*;
import cn.gooday.hrcn.common.event.events.AckEvent;
import cn.gooday.hrcn.common.event.events.CollectFinishedEvent;
import cn.gooday.hrcn.common.event.events.DataCollectedEvent;
import cn.gooday.hrcn.common.event.events.DataReceivedEvent;
import cn.gooday.hrcn.common.exception.CacheException;
import cn.gooday.hrcn.common.util.StringUtil;
import sun.misc.BASE64Encoder;

import java.util.List;


/**
 * [缓存管理服务类]
 * 实现缓存的读取，写入和删除操作
 *
 * @ProjectName: [hrcn]
 * @Author: [lixu]
 * @CreateDate: [2015/3/1 22:46]
 * @Update: [说明本次修改内容] BY[lixu][2015/3/4]
 * @Version: [v1.0]
 */
public class CacheService {
    private ICache cache;

    public CacheService(ICache cache) {
        this.cache = cache;
    }

    /**
     * 将事件添加到缓存中
     *
     * @param event
     * @return
     */
    public String add(Event<BaseBean> event) {
        try {
            EnumEventType eventType = event.getEventType();
            String eventNo = event.getEventNo();
            switch (eventType) {
                case DATA_COLLECTED_EVENT:
                case DATA_RECEIVED_EVENT:
                    eventNo = cache.add(event);
                    break;
                case COLLECT_FINISHED_EVENT:
                    Event<String> storeEvent = cache.get(eventNo , EnumEventType.DATA_COLLECTED_EVENT ,event.getEventObject());
                    if (storeEvent != null) {
                        BaseBean bean = event.getEventObject();
                        String prefix = (new BASE64Encoder()).encode(StringUtil.join(bean.getServerIp() , "&" , bean.getTomcatName()).getBytes());
                        boolean result = cache.rename(event.getEventNo(), EnumEventType.DATA_COLLECTED_EVENT, eventType ,prefix);
                        if (result) {
                            eventNo = event.getEventNo();
                        } else {
                            throw new CacheException(StringUtil.join("The Event Object : [", eventType.name(), ":", eventNo, "] cannot be processed by cache Service!"));
                        }
                    } else {
                        eventNo = cache.add(event);
                    }
                    break;
                default:
                    throw new CacheException(StringUtil.join("The Event type [", eventType.name(), "] cannot supported by cache service!"));
            }
            return eventNo;
        } catch (Exception e) {

            return null;
        }
    }

    /**
     * 按照事件类型和ID号来移除缓存系统中的缓存数据
     *
     * @param event
     */
    public void remove(Event event) {
        String exceptionInfo = "Cache Service cannot handle the event , the reason is :";
        if (event instanceof AckEvent) {
            AckEvent ackEvent = (AckEvent) event;
            EnumEventType srcEventType = ackEvent.getSrcEventType();
            String eventNo = ackEvent.getEventNo();
            if (StringUtil.isBlank(eventNo)) {
                throw new CacheException(StringUtil.join(exceptionInfo, " The event not include  eventNo!"));
            }
            if (srcEventType == null) {
                throw new CacheException(StringUtil.join(exceptionInfo, " The event not include  srcEventType!"));
            }
            Event<BaseBean> baseEvent  = null;
            switch (srcEventType) {
                case DATA_COLLECTED_EVENT: baseEvent = new DataCollectedEvent(); break;
                case COLLECT_FINISHED_EVENT:baseEvent = new CollectFinishedEvent(); break;
                case DATA_RECEIVED_EVENT:  baseEvent = new DataReceivedEvent(); break;
                default: throw new CacheException(StringUtil.join(exceptionInfo, " The event srcEventType is ", srcEventType.name()));
            }
            baseEvent.setEventNo(eventNo);
            baseEvent.setEventType(srcEventType);
            baseEvent.setEventObject((BaseBean)event.getEventObject());
            cache.remove(baseEvent);
        }else {
            throw new CacheException(StringUtil.join(exceptionInfo, " The event is not ackEvent , this event type is ", event.getEventType().name()));
        }
    }

    /**
     * 加载缓存系统中缓存数据，封装成事件列表返回
     *
     * @return
     */
    public List<Event<BaseBean>> getEvents() {
        return cache.getAll();
    }

}