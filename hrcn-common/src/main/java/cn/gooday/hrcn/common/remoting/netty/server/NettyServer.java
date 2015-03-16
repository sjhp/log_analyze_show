package cn.gooday.hrcn.common.remoting.netty.server;

import cn.gooday.hrcn.common.exception.RemotingException;
import cn.gooday.hrcn.common.remoting.IServer;
import cn.gooday.hrcn.common.util.LogUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;

@Component
public class NettyServer implements IServer {
    private int bossGroupThreads = 3;

    private int workerGroupThreads = 5;

    private Channel channel;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    @Override
    public void start(final int port) {
        bossGroup = new NioEventLoopGroup(bossGroupThreads);
        workerGroup = new NioEventLoopGroup(workerGroupThreads);
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new KryoNettyServerChannelInitializer());
        try {
            channel = serverBootstrap.bind(port).sync().channel();
            LogUtil.debug("server channel:{}",channel);
        } catch (final InterruptedException ex) {
            LogUtil.err(ex);
            throw new RemotingException(ex.getMessage());
        }
    }

    @Override
    public void close() {
        if (null == channel) {
            LogUtil.err("channel is null");
            return;
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        channel.closeFuture().syncUninterruptibly();
        bossGroup = null;
        workerGroup = null;
        channel = null;
    }
}
