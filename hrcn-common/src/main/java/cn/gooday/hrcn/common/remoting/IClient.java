/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.remoting;

import cn.gooday.hrcn.common.event.Event;

import java.net.InetSocketAddress;

/**
 * [简短描述该类的功能]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/3/3 13:50]
 * @Update: [说明本次修改内容] BY[tophawk][2015/3/3]
 * @Version: [v1.0]
 */
public interface IClient {
    /**
     * 初始化
     *
     * @param socketAddress
     */
    void init(InetSocketAddress socketAddress);

    void connect();

    boolean isConnected();

    void sent(Event obj);

    InetSocketAddress getRemoteAddress();

    void close();
}
