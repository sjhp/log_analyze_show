/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.collect.thread;

import cn.gooday.hrcn.common.collect.ICollector;

/**
 * [简短描述该类的功能]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/2/15 0:55]
 * @Update: [说明本次修改内容] BY[tophawk][2015/2/15]
 * @Version: [v1.0]
 */
public class CollectThread implements Runnable{
    protected ICollector collector;

    public void setCollector(ICollector collector) {
        this.collector = collector;
    }

    @Override
    public void run() {

    }
}
