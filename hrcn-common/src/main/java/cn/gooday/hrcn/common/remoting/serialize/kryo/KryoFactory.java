/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.remoting.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import cn.gooday.hrcn.common.util.LogUtil;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * [Kryo factory]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/3/3 16:56]
 * @Update: [说明本次修改内容] BY[tophawk][2015/3/3]
 * @Version: [v1.0]
 */
public final class KryoFactory {

    private final GenericObjectPool<Kryo> kryoPool;

    public KryoFactory() {
        kryoPool = new GenericObjectPool<>(new PooledKryoFactory());
    }

    public KryoFactory(final int maxTotal, final int minIdle, final long maxWaitMillis, final long minEvictableIdleTimeMillis) {
        kryoPool = new GenericObjectPool<>(new PooledKryoFactory());
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        if (maxTotal != 0) {
            config.setMaxTotal(maxTotal);
        } else {
            config.setMaxTotal(GenericObjectPoolConfig.DEFAULT_MAX_TOTAL);
        }

        if (minIdle != 0) {
            config.setMinIdle(minIdle);
        } else {
            config.setMinIdle(GenericObjectPoolConfig.DEFAULT_MIN_IDLE);
        }

        if (maxWaitMillis != 0) {
            config.setMaxWaitMillis(maxWaitMillis);
        } else {
            config.setMaxWaitMillis(GenericObjectPoolConfig.DEFAULT_MAX_WAIT_MILLIS);
        }

        if (minEvictableIdleTimeMillis != 0) {
            config.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        } else {
            config.setMinEvictableIdleTimeMillis(GenericObjectPoolConfig.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS);
        }
        kryoPool.setConfig(config);
    }

    public Kryo getKryo() {
        try {
            return kryoPool.borrowObject();
        } catch (final Exception ex) {
            LogUtil.err(ex);
            throw new RuntimeException(ex);
        }
    }

    public void returnKryo(final Kryo kryo) {
        kryoPool.returnObject(kryo);
    }
}

