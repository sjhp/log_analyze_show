package cn.gooday.hrcn.common.remoting.netty.server;

import cn.gooday.hrcn.common.constant.EnumEventType;
import cn.gooday.hrcn.common.event.Event;
import cn.gooday.hrcn.common.event.events.AckEvent;
import cn.gooday.hrcn.common.exception.QueueManagerException;
import cn.gooday.hrcn.common.remoting.netty.handler.BaseNettyHandlerAdaptor;
import cn.gooday.hrcn.common.util.LogUtil;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;

@Sharable
public class NettyServerDispatchHandler extends BaseNettyHandlerAdaptor {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Event event = (Event) msg;

        LogUtil.debug("received event:{}", event);

        EnumEventType sourceEventType = event.getEventType();

        //保存并发出 DataReceivedEvent
        event.setEventType(EnumEventType.DATA_RECEIVED_EVENT);
        try {
            qm.push(event);

            //发出ack
            AckEvent ack = new AckEvent(event.getEventNo(), sourceEventType);
            ctx.writeAndFlush(ack);

        } catch (QueueManagerException ex) {
            LogUtil.err("error in put Queue: {}!", ex.getMessage());
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext channelHandlerContext) throws Exception {
        super.channelRegistered(channelHandlerContext);
        LogUtil.debug("registered");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext channelHandlerContext) throws Exception {
        super.channelUnregistered(channelHandlerContext);
        LogUtil.debug("channelUnregistered");
    }

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
        super.channelActive(channelHandlerContext);
        LogUtil.debug("channelActive");
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) throws Exception {
        super.channelInactive(channelHandlerContext);
        LogUtil.debug("channelInactive");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext channelHandlerContext) throws Exception {
        super.channelReadComplete(channelHandlerContext);
        LogUtil.debug("channelReadComplete");
    }
}
