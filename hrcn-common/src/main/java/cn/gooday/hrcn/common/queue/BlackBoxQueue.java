package cn.gooday.hrcn.common.queue;

import cn.gooday.hrcn.common.event.Event;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * [记录异常事件]
 * @ProjectName: [ hrcn]
 * @Author: [xiaofei ]
 * @CreateDate: [ 2015/3/9 11:15]
 * @Update: [说明本次修改内容] BY [xiaofei ][2015/3/9]
 * @Version: [v1.0]
 */
public class BlackBoxQueue {

    /**
     * [私有化空参构造]
     */
    private BlackBoxQueue(){
    }

    /**
     * 内部类实现lazy单例
     */
    private  static class eqInstance{
        /**
         * [eq error queue]
         */
        private static BlockingQueue<Event> queue = new ArrayBlockingQueue<Event>(10000);
    }

    /**
     * [返回实例]
     * @return
     */
    public static BlockingQueue<Event> getEQ(){
        return eqInstance.queue;
    }
}
