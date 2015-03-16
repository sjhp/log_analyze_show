/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.event;

import cn.gooday.hrcn.common.constant.EnumEventType;

/**
 * [事件,避免耦合，不需要传递事件源]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/2/11 0:28]
 * @Update: [重新修改定义] BY[tophawk][2015/3/02]
 * @Version: [v1.0]
 */
public abstract class Event<T> {
    /**
     * 事件NO代替CacheObject的编号
     */
    private String eventNo;
    private EnumEventType eventType;
    private T eventObject;

    //private Object eventSource;
    public Event() {
        super();
    }

    public Event(EnumEventType eventType, T obj) {
        this.eventType = eventType;
        this.eventObject = obj;
    }
    


    public EnumEventType getEventType() {
        return eventType;
    }

    public T getEventObject() {
        return eventObject;
    }

    public void setEventType(EnumEventType eventType) {
        this.eventType = eventType;
    }

    public void setEventObject(T eventObject) {
        this.eventObject = eventObject;
    }

    public String getEventNo() {
        return eventNo;
    }

    public void setEventNo(String eventNo) {
        this.eventNo = eventNo;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventNo='" + eventNo + '\'' +
                ", eventType=" + eventType +
                ", eventObject=" + eventObject +
                '}';
    }
}
