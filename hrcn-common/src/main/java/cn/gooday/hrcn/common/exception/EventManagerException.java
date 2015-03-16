/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.exception;

import cn.gooday.hrcn.common.event.Event;
import cn.gooday.hrcn.common.event.events.ExceptionEvent;

/**
 * [事件分发异常]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/2/10 22:06]
 * @Update: [说明本次修改内容] BY[tophawk][2015/2/10]
 * @Version: [v1.0]
 */
public class EventManagerException extends BaseRuntimeException {

    /**
     * @param msg
     */
    public EventManagerException(String msg, Event event1) {
        super(msg);

        addExceptionEvent(event1);
        //TO DO: 可以选择发往服务器报警
    }
}
