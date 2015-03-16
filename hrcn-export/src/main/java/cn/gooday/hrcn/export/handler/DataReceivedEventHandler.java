/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.export.handler;

import cn.gooday.hrcn.analyze.LogAnalyzer;
import cn.gooday.hrcn.analyze.title.TitleAnalyzer;
import cn.gooday.hrcn.common.bean.BaseBean;
import cn.gooday.hrcn.common.event.Event;
import cn.gooday.hrcn.common.event.events.AckEvent;
import cn.gooday.hrcn.common.event.handler.BaseHandler;
import cn.gooday.hrcn.common.event.handler.IEventHandler;
import cn.gooday.hrcn.common.exception.HandlerException;
import cn.gooday.hrcn.common.util.LogUtil;
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
public class DataReceivedEventHandler extends BaseHandler implements IEventHandler<BaseBean> {

  @Override
    public void handle(Event<BaseBean> event) {
        //接收 DataReceivedEvent,并处理
      LogUtil.debug("===================received:{}",event);
      LogAnalyzer logAnalyzer = LogAnalyzer.getInstance();
      try {
          boolean flag = logAnalyzer.analyze(event);
          if(flag){
              AckEvent ackEvent = new AckEvent(event.getEventNo(),event.getEventType());
              qm.push(ackEvent);
          }else {
              LogUtil.err("日志解析出错！");
          }
      }catch(Exception ex){
          //TODO push to trash=done 发出异常后会被em捕获
          throw new HandlerException(StringUtil.join("===failed to analyze data ", event.toString()));
      }
    }
}
