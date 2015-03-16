/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.remoting.factory;

import cn.gooday.hrcn.common.remoting.IClient;
import cn.gooday.hrcn.common.remoting.IServer;
import cn.gooday.hrcn.common.remoting.netty.factory.NettyRemotingFactory;

/**
 * [简短描述该类的功能]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/3/3 14:11]
 * @Update: [说明本次修改内容] BY[tophawk][2015/3/3]
 * @Version: [v1.0]
 */
public abstract class RemotingFactory {
    private static final RemotingFactory factory = new NettyRemotingFactory();

    public static RemotingFactory getDefaultFactory() {
        return factory;
    }

    public abstract IClient createClient();

    public abstract IServer createServer();
}
