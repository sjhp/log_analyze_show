package cn.gooday.hrcn.common.remoting.netty.server;

import cn.gooday.hrcn.common.remoting.netty.codec.KryoDecoder;
import cn.gooday.hrcn.common.remoting.netty.codec.KryoEncoder;
import cn.gooday.hrcn.common.remoting.netty.codec.KryoPool;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class KryoNettyServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private NettyServerDispatchHandler serverDispatchHandler;

    private KryoPool kryoSerializationFactory;

    public KryoNettyServerChannelInitializer(){
         kryoSerializationFactory = new KryoPool();
    }

    protected void initChannel(final SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new KryoEncoder(kryoSerializationFactory));
        ch.pipeline().addLast(new KryoDecoder(kryoSerializationFactory));
        ch.pipeline().addLast(new NettyServerDispatchHandler());
    }
}
