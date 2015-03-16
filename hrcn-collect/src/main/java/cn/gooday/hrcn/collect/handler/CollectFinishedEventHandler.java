/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.collect.handler;

import cn.gooday.hrcn.common.constant.Constants;
import cn.gooday.hrcn.common.constant.EnumEventType;
import cn.gooday.hrcn.common.context.RuntimeContext;
import cn.gooday.hrcn.common.event.Event;
import cn.gooday.hrcn.common.event.handler.BaseHandler;
import cn.gooday.hrcn.common.event.handler.IEventHandler;
import cn.gooday.hrcn.common.exception.HandlerException;
import cn.gooday.hrcn.common.exception.RemotingException;
import cn.gooday.hrcn.common.remoting.IClient;
import cn.gooday.hrcn.common.remoting.factory.RemotingFactory;
import cn.gooday.hrcn.common.util.LogUtil;
import cn.gooday.hrcn.common.util.RtUtil;
import cn.gooday.hrcn.common.util.StringUtil;

/**
 * [处理数据收集CollectFinished事件,用于远程发送数据]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/2/11 1:10]
 * @Update: [说明本次修改内容] BY[tophawk][2015/2/11]
 * @Version: [v1.0]
 */
public class CollectFinishedEventHandler extends BaseHandler implements IEventHandler<String> {
    private IClient client;


    public void setClient(IClient client) {
        this.client = client;
    }
    public CollectFinishedEventHandler(){
        super();
        initClient();
    }

    public void initClient(){
        //TODO need 断线重连，发送失败重试机制 =>OK
        client = RemotingFactory.getDefaultFactory().createClient();
        setClient(client);
    }

    @Override
    public void handle(Event event) {
        //接收 CollectFinishedEvent,并处理
        EnumEventType sourceEventType = event.getEventType();
        LogUtil.debug("received:{}",sourceEventType);
        //TODO 远程发送,在这里扩展容错机制=>done

        boolean sent = sendEvent(event);

        //失败重试
        if(!sent) {

            int retry = RtUtil.getConfigService().getInt("remote.retry");
            if (retry == 0) retry = Constants.SYSTEM_FAIL_RETRY;

            int count = retry;
            while(count-->0 ||(sent=sendEvent(event))){
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
                throw new HandlerException(StringUtil.join("===finally failed to send data ",event.toString()));
            }
        }
    }

    /**
     * 远程发送消息
     * @return
     */
    private boolean sendEvent(Event event){
        try {
            client.sent(event);
            return true;
        }catch (RemotingException ex){
            LogUtil.err(ex);
            return false;
        }
    }
}
