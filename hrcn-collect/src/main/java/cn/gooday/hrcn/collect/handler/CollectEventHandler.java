/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.collect.handler;

import cn.gooday.hrcn.common.constant.Constants;
import cn.gooday.hrcn.common.constant.EnumEventType;
import cn.gooday.hrcn.common.event.Event;
import cn.gooday.hrcn.common.event.events.AckEvent;
import cn.gooday.hrcn.common.event.handler.BaseHandler;
import cn.gooday.hrcn.common.event.handler.IEventHandler;
import cn.gooday.hrcn.common.exception.HandlerException;
import cn.gooday.hrcn.common.exception.QueueManagerException;
import cn.gooday.hrcn.common.util.LogUtil;
import cn.gooday.hrcn.common.util.RtUtil;
import cn.gooday.hrcn.common.util.StringUtil;

/**
 * [处理数据收集DataCollected事件]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/2/11 1:10]
 * @Update: [说明本次修改内容] BY[tophawk][2015/2/11]
 * @Version: [v1.0]
 */
public class CollectEventHandler extends BaseHandler implements IEventHandler {

    @Override
    public void handle(Event event) {
        //接收 DataCollectedEvent,并处理

        EnumEventType sourceEventType = event.getEventType();

        //发出 CollectFinishedEvent
        event.setEventType(EnumEventType.COLLECT_FINISHED_EVENT);

        boolean sent = sendEvent(event,sourceEventType);

        //失败重试
        if(!sent) {

            int retry = RtUtil.getConfigService().getInt("collect.retry");
            if (retry == 0) retry = Constants.SYSTEM_FAIL_RETRY;

            int count = retry;
            while(count-->0 ||(sent=sendEvent(event,sourceEventType))){
                try {
                    Thread.sleep(Constants.SYSTEM_FAIL_RETRY_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(count <=0 && (!sent)){
                //放入处理失败队列
                LogUtil.err("all of {} retries of the data {} is fail,push to trash!",retry,event);
                //TODO push to trash=done 发出异常后会被em捕获
                throw new HandlerException(StringUtil.join("finally failed to collect data ", event.toString()));
            }
        }
    }

    private boolean sendEvent(Event event,EnumEventType sourceEventType){
        try {
            qm.push(event);

            //发出ack
            AckEvent ack = new AckEvent(event.getEventNo(), sourceEventType);
            qm.push(ack);

            return true;
        } catch (QueueManagerException ex) {
            LogUtil.err(ex);
            return false;
        }
    }
}
