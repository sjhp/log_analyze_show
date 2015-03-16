package cn.gooday.hrcn.common.event.events;

import cn.gooday.hrcn.common.constant.EnumEventType;
import cn.gooday.hrcn.common.event.Event;

/**
 * @ProjectName: [ hrcn]
 * @Author: [xiaofei ]
 * @CreateDate: [ 2015/3/9 11:26]
 * @Update: [说明本性修改内容] BY [xiaofei ][2015/3/9]
 * @Version: [v1.0]
 */
public class ExceptionEvent extends Event{
    /**
     * 时间戳
     */
    private String timestamp;
    /**
     * 事件
     */
    private Event event;

    /**
     * 错误原因
     */
    private String cause;


    public ExceptionEvent(String cause, Event event) {
        this.cause = cause;
        this.event = event;
    }

    /**
     * [获取时间戳]
     * @return
     */
    public String getTimestamp() {
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     * 获取事件
     * @return
     */
    public Event getEvent() {
        return event;
    }

    /**
     * 设置事件
     * @param event
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }
}
