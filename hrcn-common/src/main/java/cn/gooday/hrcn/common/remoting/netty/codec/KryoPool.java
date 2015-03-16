/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.remoting.netty.codec;

import cn.gooday.hrcn.common.remoting.serialize.kryo.KryoFactory;
import cn.gooday.hrcn.common.remoting.serialize.kryo.KryoSerialization;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

/**
 * [序列化池]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/3/4 10:16]
 * @Update: [说明本次修改内容] BY[tophawk][2015/3/4]
 * @Version: [v1.0]
 */
public class KryoPool {

    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

    private KryoFactory kryoFactory;

    private int maxTotal;

    private int minIdle;

    private long maxWaitMillis;

    private long minEvictableIdleTimeMillis;

    public KryoPool() {
        init();
    }

    public void init() {
        kryoFactory = new KryoFactory(maxTotal, minIdle, maxWaitMillis, minEvictableIdleTimeMillis);
    }

    public void encode(final ByteBuf out, final Object message) throws IOException {
        ByteBufOutputStream bout = new ByteBufOutputStream(out);
        bout.write(LENGTH_PLACEHOLDER);
        KryoSerialization kryoSerialization = new KryoSerialization(kryoFactory);
        kryoSerialization.serialize(bout, message);
    }

    public Object decode(final ByteBuf in) throws IOException {
        KryoSerialization kryoSerialization = new KryoSerialization(kryoFactory);
        return kryoSerialization.deserialize(new ByteBufInputStream(in));
    }
}
