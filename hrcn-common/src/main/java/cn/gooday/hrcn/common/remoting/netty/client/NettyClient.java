/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.remoting.netty.client;

import cn.gooday.hrcn.common.event.Event;
import cn.gooday.hrcn.common.exception.RemotingException;
import cn.gooday.hrcn.common.remoting.IClient;
import cn.gooday.hrcn.common.util.LogUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * [Netty Client]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/3/4 10:53]
 * @Update: [说明本次修改内容] BY[tophawk][2015/3/4]
 * @Version: [v1.0]
 */
public final class NettyClient implements IClient,Runnable{
    /**
     * 工作线程数
     */
    private int workerGroupThreads = 5;
    /**
     * 连接状态
     */
    private volatile boolean connecting;
    private InetSocketAddress addr;
    private Bootstrap bootstrap;
    private EventLoopGroup workerGroup;
    private Channel channel;

    @Override
    public void init(InetSocketAddress socketAddress) {
        addr = socketAddress;
        workerGroup = new NioEventLoopGroup(workerGroupThreads);
        bootstrap = new Bootstrap();
        bootstrap
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(new KryoNettyClientChannelInitializer());
    }

    @Override
    public void run() {
        this.connect();
    }

    @Override
    public void connect() {
        LogUtil.debug("client connecting!");
        try {
            channel = bootstrap.connect(addr.getAddress().getHostAddress(), addr.getPort()).syncUninterruptibly().channel();
            LogUtil.debug("client connected successful to the server {}!", addr);
        }catch (Exception ex){
            LogUtil.err("client failed to connect to server",ex);
        }finally {
            connecting = false;
        }
    }

    @Override
    public boolean isConnected() {
        LogUtil.debug("===go isConnected={}!",channel == null?false:channel.isActive());
        return channel == null?false:channel.isActive();
    }

    @Override
    public void sent(Event obj) {
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(obj);
            LogUtil.debug("msg sent:{}", obj);
        } else {
            if(!connecting) {
                LogUtil.debug("++++do connecting...");
                connecting = true;
                workerGroup.schedule(this,2l, TimeUnit.SECONDS);
            }else{
                LogUtil.debug("++++already connecting...");
            }
            if (channel != null) channel.closeFuture().syncUninterruptibly();
            throw new RemotingException("channel is not active!");
        }
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return addr;
    }

    @Override
    public void close() {
        workerGroup.shutdownGracefully();
        if (channel != null) {
            channel.closeFuture().syncUninterruptibly();
        }
        workerGroup = null;
        channel = null;
    }
}

