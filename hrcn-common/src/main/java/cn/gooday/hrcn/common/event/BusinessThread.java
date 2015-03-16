package cn.gooday.hrcn.common.event;

import cn.gooday.hrcn.common.util.LogUtil;

/**
 * @ProjectName: [ hrcn]
 * @Author: [xiaofei ]
 * @CreateDate: [ 2015/3/2 18:33]
 * @Update: [说明本次修改内容] BY [xiaofei ][2015/3/2]
 * @Version: [v1.0]
 */
public abstract class BusinessThread extends Thread {

    /**
     * [记录没有catch的异常]
     */
    public BusinessThread() {
        Thread.setDefaultUncaughtExceptionHandler(
                new UncaughtExceptionHandler() {
                    @Override  public void uncaughtException(Thread t, Throwable e) {
                        LogUtil.err(t.getName() + " {error/reason}: {" + e + "}");
                    }
                });
         this.start();
    }
    public void run(){
    }
    /**
     * [获取线程的优先级]
     *
     * @return
     */
    public int getPriorityValue(){
        return 0;
    }

    /**
     * [获取线程的间隔时间]
     *
     * @return
     */
    public int getIntervalTime() {
        return 0;
    }
}
