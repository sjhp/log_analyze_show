package cn.gooday.hrcn.common.schedule;

import cn.gooday.hrcn.common.config.ConfigService;
import cn.gooday.hrcn.common.constant.Constants;
import cn.gooday.hrcn.common.context.RuntimeContext;
import cn.gooday.hrcn.common.event.Event;
import cn.gooday.hrcn.common.exception.ScheduleException;
import cn.gooday.hrcn.common.notify.INotify;
import cn.gooday.hrcn.common.util.LogUtil;
import cn.gooday.hrcn.common.util.RtUtil;
import cn.gooday.hrcn.common.util.StatUtil;
import org.springframework.scheduling.SchedulingException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: [ hrcn]
 * @Author: [xiaofei ]
 * @CreateDate: [ 2015/2/26 10:30]
 * @Update: [说明本次修改内容] BY [xiaofei ][2015/2/26]
 * @Version: [v1.0]
 */
public class ScheduledExcutor {

    /**
     * 读取配置
     */
    private static ConfigService configService = RuntimeContext.get(Constants.CONTEXT_CONFIGSERVICE);
    /**
     * 线程数
     */
    private static final int SCHEDULE_THREADS = configService.getInt("SCHEDULE_THREADS");
    /**
     * 业务，第二个参数为上次调用时间
     */
    private static Map<INotify, Long> businesses;

    /**
     * 是否暂停
     */
    public static boolean isPause = false;

    public ScheduledExcutor() {
        init();
    }

    /**
     * 资源初始化
     */
    private static void init() {
        businesses = new HashMap<INotify, Long>();
    }

    /**
     * [注册业务]
     *
     * @param business
     * @return
     */
    public boolean register(INotify business) throws SchedulingException {
        //存入当前时间，为默认调用时间
        businesses.put(business, System.currentTimeMillis());
        return true;
    }

    /**
     * [业务调度]
     *
     * @Return: void
     */
    public void scheduledBusiness() throws SchedulingException {

        //定义一个调度器定时执行采集器
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(SCHEDULE_THREADS);
        //定义执行类
        ScheduleJob jobThread = new ScheduleJob(businesses);
        try {
            //以固定频率调用job，在job里轮询每个listener，对符合条件的，逐个notify
            long interval = RtUtil.getConfigService().getLong("schedule.interval");
            if (interval == 0) interval = 500;
            scheduledThreadPool.scheduleAtFixedRate(
                    jobThread,
                    500,
                    interval,
                    TimeUnit.MILLISECONDS);
        } catch (RuntimeException ex) {
            //LogUtil.err("schedule job start failed!");
            throw new ScheduleException("schedule job start failed!");
        }
    }

    /**
     * [报警]
     *
     * @param e
     */
    public void warning(Event e) {
        LogUtil.warn("NO:" + e.getEventNo() + " TYPE:" + e.getEventType() + " scheduled failed");
        isPause = true;
    }

    public static boolean isIsPause() {
        return isPause;
    }


}
