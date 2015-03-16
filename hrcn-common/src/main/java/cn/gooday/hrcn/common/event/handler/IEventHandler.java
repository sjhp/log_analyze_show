/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.event.handler;

import cn.gooday.hrcn.common.event.Event;

/**
 * [事件处理接口]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/2/11 1:07]
 * @Update: [说明本次修改内容] BY[tophawk][2015/2/11]
 * @Version: [v1.0]
 */
public interface IEventHandler<T> {
    void handle(Event<T> event);
}
