/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.remoting.netty.codec;

import cn.gooday.hrcn.common.util.LogUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * [kryo 解码功能]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/3/4 10:19]
 * @Update: [说明本次修改内容] BY[tophawk][2015/3/4]
 * @Version: [v1.0]
 */
final public class KryoDecoder extends LengthFieldBasedFrameDecoder {

    private final KryoPool kryoPool;

    public KryoDecoder(final KryoPool kryoSerializationFactory) {
        super(10485760, 0, 4, 0, 4);
        this.kryoPool = kryoSerializationFactory;
    }

    @Override
    protected Object decode(final ChannelHandlerContext ctx, final ByteBuf in) throws Exception {
        LogUtil.debug("server decode!");
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }
        try {
            return kryoPool.decode(frame);
        } finally {
            if (null != frame) {
                frame.release();
            }
        }
    }
}
