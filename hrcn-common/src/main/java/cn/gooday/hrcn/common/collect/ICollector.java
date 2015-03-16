/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.collect;

import cn.gooday.hrcn.common.exception.CollectException;

/**
 * [收集接口]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/2/11 0:00]
 * @Update: [说明本次修改内容] BY[tophawk][2015/2/11]
 * @Version: [v1.0]
 */
public interface ICollector<T> {
    void collect(T data) throws CollectException;
}
