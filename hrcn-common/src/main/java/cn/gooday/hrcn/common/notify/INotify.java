/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.notify;

import cn.gooday.hrcn.common.util.LogUtil;

/**
 * [notify接口]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/3/2 17:09]
 * @Update: [说明本次修改内容] BY[tophawk][2015/3/2]
 * @Version: [v1.0]
 */
public interface INotify{
    /**
     * 定时回调接口
     * @param key
     */
    public void schedule(String key);

    default void uncaughtException() {
        Thread.setDefaultUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override  public void uncaughtException(Thread t, Throwable e) {
                        LogUtil.err(t.getName() + " {error/reason}: {" + e + "}");
                    }
                });
    }

    /**
     * [获取业务的优先级]
     *
     * @return
     */
    public int getPriorityValue();

    /**
     * [获取业务间隔事件]
     *
     * @return
     */
    public int getIntervalTime();
}
