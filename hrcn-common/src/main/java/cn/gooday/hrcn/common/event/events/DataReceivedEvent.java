/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.event.events;

import cn.gooday.hrcn.common.constant.EnumEventType;
import cn.gooday.hrcn.common.event.Event;

/**
 * [输出事件,避免耦合，不需要传递事件源]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/2/11 0:28]
 * @Update: [说明本次修改内容] BY[tophawk][2015/2/11]
 * @Version: [v1.0]
 */
public class DataReceivedEvent extends Event {
    public DataReceivedEvent() {
        setEventType(EnumEventType.DATA_RECEIVED_EVENT);
    }
}
