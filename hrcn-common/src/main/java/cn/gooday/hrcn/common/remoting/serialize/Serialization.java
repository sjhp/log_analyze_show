package cn.gooday.hrcn.common.remoting.serialize;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Serialization {

    void serialize(OutputStream out, Object message) throws IOException;

    Object deserialize(InputStream in) throws IOException;
}
