/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.collect.factory;


import cn.gooday.hrcn.collect.thread.CollectThread;
import cn.gooday.hrcn.common.InstanceManager;
import cn.gooday.hrcn.common.collect.ICollector;
import cn.gooday.hrcn.common.constant.Constants;
import cn.gooday.hrcn.common.context.RuntimeContext;
import cn.gooday.hrcn.common.util.LogUtil;
import cn.gooday.hrcn.common.util.StringUtil;

/**
 * [负责创建collectors]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/2/14 13:15]
 * @Update: [说明本次修改内容] BY[tophawk][2015/2/14]
 * @Version: [v1.0]
 */
public class CollectorFactory {
    /**
     * 创建主动收集 型的collector
     *
     * @param clazz
     * @return
     */
    public CollectThread create(String clazz) {
        if (StringUtil.isBlank(clazz)) {
            throw new IllegalArgumentException(StringUtil.join("Invalid class name: ", clazz));
        }
        try {
            ICollector collector = RuntimeContext.get(Constants.CONTEXT_COLLECTOR);

            CollectThread collectThread = InstanceManager.createInstance(clazz);
            collectThread.setCollector(collector);

            return collectThread;
        } catch (Exception e) {
            LogUtil.sendErr(e);
            throw new IllegalStateException(StringUtil.join("class ", clazz, " not found!"));
        }
    }
}
