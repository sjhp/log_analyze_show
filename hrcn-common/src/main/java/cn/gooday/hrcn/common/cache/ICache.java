/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.cache;

import cn.gooday.hrcn.common.bean.BaseBean;
import cn.gooday.hrcn.common.constant.EnumEventType;
import cn.gooday.hrcn.common.event.Event;
import cn.gooday.hrcn.common.exception.CacheException;

import java.util.List;

/**
 * [缓存接口]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/2/10 21:50]
 * @Update: [说明本次修改内容] BY[tophawk][2015/2/10]
 * @Version: [v1.0]
 */
public interface ICache<T> {
    /**
     * 存入数据
     *
     * @param object
     * @return
     * @throws CacheException
     */
    String add(Event<T> object) throws CacheException;

    /**
     * 根据事件编号和事件类型进行缓存数据的删除操作
     *
     * @param object
     *
     */
    void remove(Event<T> object);

    /**
     * 获取缓存数据
     *
     * @param cacheNo
     * @param eventType
     * @param baseBean
     * @return
     * @throws CacheException
     */
    Event<T> get(String cacheNo , EnumEventType eventType , BaseBean baseBean);

    /**
     * 获取所有缓存数据
     *
     * @return
     */
    List<Event<T>> getAll();

    /**
     * 缓存数据的类型进行转换
     *
     * @param eventNo
     * @param targetEventType
     * @param newEventType
     * @return
     */
    boolean rename(String eventNo, EnumEventType targetEventType, EnumEventType newEventType , String prefix);
}