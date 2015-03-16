/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.exception;

import cn.gooday.hrcn.common.event.Event;
import cn.gooday.hrcn.common.event.events.ExceptionEvent;
import cn.gooday.hrcn.common.queue.BlackBoxQueue;
import cn.gooday.hrcn.common.util.StatUtil;

import java.util.concurrent.BlockingQueue;

/**
 * [异常父类]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/2/10 22:05]
 * @Update: [说明本次修改内容] BY[tophawk][2015/2/10]
 * @Version: [v1.0]
 */
public class BaseRuntimeException extends RuntimeException {
    /**
     * 黑匣子
     */
    protected static BlockingQueue<Event> queue = BlackBoxQueue.getEQ();

    public BaseRuntimeException(Throwable t) {
        super(t);
    }

    public BaseRuntimeException(String msg) {
        super(msg);
    }

    /**
     * [发生异常构建异常事件向中控调度申请暂停]
     *
     * @param event
     */
    public void addExceptionEvent(Event event) {
        BlackBoxQueue.getEQ().add(new ExceptionEvent(this.getMessage(),event));
        StatUtil.stat(event,this);
    }
}
