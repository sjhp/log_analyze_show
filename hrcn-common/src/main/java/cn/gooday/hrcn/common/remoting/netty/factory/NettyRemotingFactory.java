/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.remoting.netty.factory;

import cn.gooday.hrcn.common.config.ConfigService;
import cn.gooday.hrcn.common.constant.Constants;
import cn.gooday.hrcn.common.context.RuntimeContext;
import cn.gooday.hrcn.common.exception.RemotingException;
import cn.gooday.hrcn.common.remoting.IClient;
import cn.gooday.hrcn.common.remoting.IServer;
import cn.gooday.hrcn.common.remoting.factory.RemotingFactory;
import cn.gooday.hrcn.common.remoting.netty.client.NettyClient;
import cn.gooday.hrcn.common.remoting.netty.server.NettyServer;
import cn.gooday.hrcn.common.util.LogUtil;
import cn.gooday.hrcn.common.util.StringUtil;
import org.springframework.util.StringUtils;

import java.net.ConnectException;
import java.net.InetSocketAddress;

/**
 * [简短描述该类的功能]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/3/3 14:11]
 * @Update: [说明本次修改内容] BY[tophawk][2015/3/3]
 * @Version: [v1.0]
 */
public class NettyRemotingFactory extends RemotingFactory {
    private IClient client;
    public IClient createClient() {
        if(null != client) return client;

        IClient client = new NettyClient();
        //TODO 活化此处,集群化配置，已经做完
        ConfigService  configService = RuntimeContext.get(Constants.CONTEXT_CONFIGSERVICE);
        String serverAddr = configService.getString("server.addr");
        if(StringUtil.isBlank(serverAddr)){
            throw new RemotingException("server address config error,please check! ");
        }
        String[] addrs = StringUtils.split(serverAddr,",");
        for(String addr:addrs){
            String[] ipInfo = StringUtils.split(addr,":");
            if(ipInfo.length != 2){
                throw new RemotingException("server address config error,please check! ");
            }

            //连接服务器
            InetSocketAddress socketAddress = new InetSocketAddress(ipInfo[0],Integer.valueOf(ipInfo[1]));
            try {
                client.init(socketAddress);
                client.connect();
                break;
            }catch (Exception ex){
                LogUtil.err(StringUtil.join("fail to connect to host ",addr,"!"));
                client.close();
            }
        }
        if(client.isConnected()) {
            return client;
        }else{
            throw new RemotingException("fail to connect to all servers!");
        }
    }

    public IServer createServer() {
        IServer server = new NettyServer();
        return server;
    }
}
