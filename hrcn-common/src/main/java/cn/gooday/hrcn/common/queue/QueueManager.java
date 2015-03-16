/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.queue;

import cn.gooday.hrcn.common.bean.BaseBean;
import cn.gooday.hrcn.common.cache.service.CacheService;
import cn.gooday.hrcn.common.config.ConfigService;
import cn.gooday.hrcn.common.constant.Constants;
import cn.gooday.hrcn.common.constant.EnumEventType;
import cn.gooday.hrcn.common.context.RuntimeContext;
import cn.gooday.hrcn.common.event.Event;
import cn.gooday.hrcn.common.exception.QueueManagerException;
import cn.gooday.hrcn.common.util.LogUtil;
import cn.gooday.hrcn.common.util.StringUtil;

import java.text.DecimalFormat;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * [事件队列管理器]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/3/2 18:58]
 * @Update: [说明本次修改内容] BY[tophawk][2015/3/2]
 * @Version: [v1.0]
 */
public class QueueManager {
    /**
     * 读取配置
     */
    private  ConfigService configService = RuntimeContext.get(Constants.CONTEXT_CONFIGSERVICE);
    /**
     * 最大容积 从配置中获取
     */
    private  final int MAX_QUEUE_SIZE = configService.getInt("MAX_QUEUE_SIZE");
    /**
     * 缓存service
     */
    private  CacheService cacheService = RuntimeContext.get(Constants.CONTEXT_CACHESERVICE);
    /**
     * 事件队列
     */
    BlockingQueue<Event> queue = new ArrayBlockingQueue<Event>(10000);

    public QueueManager() {
        init();
    }

    private void init() throws QueueManagerException {
        //读数据，初始化
        for(Event<BaseBean> event:cacheService.getEvents()){
            put(event);
        }
    }

    /**
     * [把事件加到BlockingQueue里,如果BlockQueue没有空间,则调用此方法的线程被阻断
     * 直到BlockingQueue里面有空间再继续.]
     *
     * @param item
     * @throws InterruptedException
     */
    public synchronized void put(final Event item) throws QueueManagerException {
        try {
            while (queue.size() == MAX_QUEUE_SIZE) {
                LogUtil.warn("the queue is full");
                Thread.sleep(2000);
            }
            //首先存入File缓存  ack事件不进行缓存
            if (!item.getEventType().equals(EnumEventType.ACK_EVENT)) {
                cacheService.add(item);
            }else{
                cacheService.remove(item);
            }
            //放入队列
            queue.put(item);
        } catch (InterruptedException e) {
            LogUtil.err("push event failed{}", e.getMessage());
        }
        notifyAll();
    }

    /**
     * [BlockingQueue未满即可添加]
     *
     * @param item
     * @throws InterruptedException
     */
    public synchronized void offer(final Event item) throws QueueManagerException {
        try {
            while (queue.size() == MAX_QUEUE_SIZE) {
                LogUtil.info("the queue is full");
                wait();
            }
            //首先存入File缓存  ack事件不进行缓存
            if (!item.getEventType().equals(EnumEventType.ACK_EVENT)) {
                cacheService.add(item);
            }else{
                cacheService.remove(item);
            }
            //放入队列
            queue.offer(item);
        } catch (InterruptedException e) {
            LogUtil.err("push event failed{}", e.getMessage());
        }
        notifyAll();
    }

    /**
     * [可以设定等待的时间，如果在指定的时间内，还不能往队列中加入BlockingQueue，则返回失败]
     *
     * @param item
     * @throws InterruptedException
     */
    public synchronized void offer(final Event item, long timeout, TimeUnit unit) throws QueueManagerException {
        try {
            while (queue.size() == MAX_QUEUE_SIZE) {
                LogUtil.info("the queue is full");
                wait();
            }
            //首先存入File缓存  ack事件不进行缓存
            if (!item.getEventType().equals(EnumEventType.ACK_EVENT)) {
                cacheService.add(item);
            }else{
                cacheService.remove(item);
            }
            //放入队列
            queue.offer(item, timeout, unit);
        } catch (InterruptedException e) {
            LogUtil.err("push event failed{}", e.getMessage());
        }
        notifyAll();
    }

    /**
     * [取走BlockingQueue里排在首位的对象,若BlockingQueue为空,阻断进入等待状态直到BlockingQueue有新的数据被加入; ]
     *
     * @return Event
     * @throws InterruptedException
     */
    public synchronized Event take() throws QueueManagerException {
        Event event = null;
        try {
            while (queue.size() == 0) {
                wait();
            }
            event = queue.take();
            LogUtil.info(StringUtil.join(event.getEventNo()+":"+event.getEventType()+" dequeue successful"));
        } catch (InterruptedException e) {
            LogUtil.err("pop event failed{}", e.getMessage());
        }
        return event;
    }

    /**
     * [取走BlockingQueue里排在首位的对象,若不能立即取出,则可以等time参数规定的时间,取不到时返回null]
     *
     * @return
     * @throws InterruptedException
     */
    public synchronized Event poll() throws QueueManagerException {
        Event event = null;
        try {
            while (queue.size() == 0) {
                LogUtil.info("queue is empty! dequeue failed");
                wait();
            }
            event = queue.poll();
        } catch (InterruptedException e) {
            LogUtil.err("pop event failed{}", e.getMessage());
        }
        return event;
    }

    /**
     * [从BlockingQueue取出一个队首的对象，如果在指定时间内，队列一旦有数据可取，则立即返回队列中的数据。否则知道时间超时还没有数据可取，返回失败]
     *
     * @return
     * @throws InterruptedException
     */
    public synchronized Event poll(long timeout, TimeUnit unit) throws QueueManagerException {
        Event event = null;
        try {
            while (queue.size() == 0) {
                LogUtil.info("queue is empty! dequeue failed");
                wait();
            }
            event = queue.poll();
        } catch (InterruptedException e) {
            LogUtil.err("pop event failed{}", e.getMessage());
        }
        return event;
    }

    /**
     * 移除事件
     * @param event
     */
    public void remove(Event event){
        queue.remove(event);
    }
    /**
     * [返回队列数量]
     *
     * @return
     */
    public synchronized int getSize() {
        return queue.size();
    }

    /**
     * [判断事件队列是否为空]
     *
     * @return 布尔
     */
    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * [判断事件队列是否已满]
     *
     * @return 布尔
     */
    public synchronized boolean isFull() {
        return queue.size() == MAX_QUEUE_SIZE;
    }

    public Event pop() throws QueueManagerException {
        Event event = queue.poll();
        return event;
    }

    public void push(Event object) throws QueueManagerException {
        try {
            LogUtil.debug("qm received:{}",object.toString());
            put(object);
        } catch (QueueManagerException e) {
            e.printStackTrace();
            throw new QueueManagerException(e);
        }
    }
}
