/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.util;

import cn.gooday.hrcn.common.config.ConfigService;
import cn.gooday.hrcn.common.constant.Constants;
import cn.gooday.hrcn.common.context.RuntimeContext;
import cn.gooday.hrcn.common.queue.QueueManager;

/**
 * [简化从RuntimeContext取对象的写法]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/3/9 20:20]
 * @Update: [说明本次修改内容] BY[tophawk][2015/3/9]
 * @Version: [v1.0]
 */
public class RtUtil {
    private RtUtil() {
    }

    /**
     * 获取全局的configService
     * @return
     */
    public static ConfigService getConfigService(){
        return RuntimeContext.get(Constants.CONTEXT_CONFIGSERVICE);
    }

    /**
     * 获取全局的QueueManager
     * @return
     */
    public static QueueManager getQueueManager(){
        return RuntimeContext.get(Constants.CONTEXT_QUEUEMANAGER);
    }
    public static boolean isServer(){
        return RuntimeContext.get(Constants.CONTEXT_ISSERVER);
    }
}
