/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.event.handler;

import cn.gooday.hrcn.common.constant.Constants;
import cn.gooday.hrcn.common.context.RuntimeContext;
import cn.gooday.hrcn.common.queue.QueueManager;

/**
 * [事件处理基类]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/2/11 1:07]
 * @Update: [说明本次修改内容] BY[tophawk][2015/2/11]
 * @Version: [v1.0]
 */
public class BaseHandler {
    public QueueManager qm;
    public BaseHandler(){
        this.qm = RuntimeContext.get(Constants.CONTEXT_QUEUEMANAGER);
    }
}
