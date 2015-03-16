/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.collect.impl;

import cn.gooday.hrcn.common.bean.BaseBean;
import cn.gooday.hrcn.common.cache.service.CacheService;
import cn.gooday.hrcn.common.collect.ICollector;
import cn.gooday.hrcn.common.constant.Constants;
import cn.gooday.hrcn.common.context.RuntimeContext;
import cn.gooday.hrcn.common.event.Event;
import cn.gooday.hrcn.common.event.events.DataCollectedEvent;
import cn.gooday.hrcn.common.exception.CollectException;
import cn.gooday.hrcn.common.queue.QueueManager;
import cn.gooday.hrcn.common.util.StringUtil;

/**
 * [简短描述该类的功能]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/2/14 21:20]
 * @Update: [说明本次修改内容] BY[tophawk][2015/2/14]
 * @Version: [v1.0]
 */
public class DefaultDataCollector implements ICollector<BaseBean> {
    private CacheService cacheService;

    public DefaultDataCollector() {
    }

    public DefaultDataCollector(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    /**
     * 加入缓存
     *
     * @param baseBean
     * @throws CollectException
     */
    @Override
    public void collect(BaseBean baseBean) throws CollectException {
        if (!StringUtil.isBlank(baseBean.getContent())) {
            //cacheService.add(EnumCacheType.CACHE_COLLECT,data);
            QueueManager qm = RuntimeContext.get(Constants.CONTEXT_QUEUEMANAGER);
            Event e = new DataCollectedEvent();

            e.setEventObject(baseBean);
            qm.push(e);
        }
    }
}
