/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.schedule;

import cn.gooday.hrcn.common.notify.INotify;
import cn.gooday.hrcn.common.util.LogUtil;
import cn.gooday.hrcn.common.util.StringUtil;

import java.util.Map;

/**
 * [调度工作类]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/3/12 17:51]
 * @Update: [说明本次修改内容] BY[tophawk][2015/3/12]
 * @Version: [v1.0]
 */
public class ScheduleJob implements Runnable {
    private Map<INotify, Long> businesses;

    public ScheduleJob(Map<INotify, Long> businesses) {
        this.businesses = businesses;
    }

    @Override
    public void run() {
        //逐个通知订阅用户
        for (Map.Entry<INotify, Long> notifyListener : businesses.entrySet()) {
            try {
                //TODO 可以扩展各种key和时间表来有条件的进行notify
                long currentTimeMillis = System.currentTimeMillis();
                //通知实例
                INotify listener = notifyListener.getKey();
                //上次调度时间
                long lastTime = notifyListener.getValue();
                //间隔时间
                long notifyInterval = listener.getIntervalTime();
                //时间对比
                if (currentTimeMillis - lastTime >= notifyInterval) {
                    //达到运行条件
                    listener.schedule("");
                    businesses.put(listener, System.currentTimeMillis());
                    //LogUtil.debug("the scheduled object {} is called!", listener);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.err(StringUtil.join("{error/where}", "{", e.getMessage(), "/", "ScheduleExecutor}"));
            }
        }
    }
}
