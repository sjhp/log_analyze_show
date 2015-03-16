/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.remoting.serialize.kryo;

import cn.gooday.hrcn.common.bean.BaseBean;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import cn.gooday.hrcn.common.constant.EnumEventType;
import cn.gooday.hrcn.common.event.Event;
import cn.gooday.hrcn.common.event.events.*;
import de.javakaffee.kryoserializers.*;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [简短描述该类的功能]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/3/3 16:54]
 * @Update: [说明本次修改内容] BY[tophawk][2015/3/3]
 * @Version: [v1.0]
 */
final class PooledKryoFactory extends BasePooledObjectFactory<Kryo> {
    @Override
    public Kryo create() throws Exception {
        return createKryo();
    }

    @Override
    public PooledObject<Kryo> wrap(Kryo kryo) {
        return new DefaultPooledObject<Kryo>(kryo);
    }

    private Kryo createKryo() {
        Kryo kryo = new KryoReflectionFactorySupport() {

            @Override
            public Serializer<?> getDefaultSerializer(@SuppressWarnings("rawtypes") final Class clazz) {
                if (EnumMap.class.isAssignableFrom(clazz)) {
                    return new EnumMapSerializer();
                }
                if (SubListSerializers.ArrayListSubListSerializer.canSerialize(clazz) || SubListSerializers.JavaUtilSubListSerializer.canSerialize(clazz)) {
                    return SubListSerializers.createFor(clazz);
                }
                return super.getDefaultSerializer(clazz);
            }
        };

        /**
         * 必须注册
         */
        kryo.setRegistrationRequired(true);

        kryo.register(Arrays.asList("").getClass(), new ArraysAsListSerializer());
        UnmodifiableCollectionsSerializer.registerSerializers(kryo);
        SynchronizedCollectionsSerializer.registerSerializers(kryo);

        // now just added some very common classes
        // TODO optimization
        kryo.register(HashMap.class);
        kryo.register(ArrayList.class);
        kryo.register(LinkedList.class);
        kryo.register(HashSet.class);
        kryo.register(TreeSet.class);
        kryo.register(Hashtable.class);
        kryo.register(Date.class);
        kryo.register(Calendar.class);
        kryo.register(ConcurrentHashMap.class);
        kryo.register(SimpleDateFormat.class);
        kryo.register(GregorianCalendar.class);
        kryo.register(Vector.class);
        kryo.register(BitSet.class);
        kryo.register(StringBuffer.class);
        kryo.register(StringBuilder.class);
        kryo.register(Object.class);
        kryo.register(Object[].class);
        kryo.register(String[].class);
        kryo.register(byte[].class);
        kryo.register(char[].class);
        kryo.register(int[].class);
        kryo.register(float[].class);
        kryo.register(double[].class);

        //register biz class,此处要改为根据注解来注册
        kryo.register(AckEvent.class);
        kryo.register(DataCollectedEvent.class);
        kryo.register(ConfigUpdateEvent.class);
        kryo.register(CollectFinishedEvent.class);
        kryo.register(DataReceivedEvent.class);
        kryo.register(StatusEvent.class);
        kryo.register(Event.class);
        kryo.register(EnumEventType.class);
        kryo.register(BaseBean.class);
        kryo.register(ExceptionEvent.class);


        return kryo;
    }
}

