/**
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.util;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * [简短描述该类的功能]
 *
 * @ProjectName: [hrcn]
 * @Author: [admin]
 * @CreateDate: [2015/2/28 14:48]
 * @Update: [说明本次修改内容] BY[admin][2015/2/28]
 * @Version: [v1.0]
 */
public class DataCompression {

        /*
         * 压缩字符串为 byte[] 储存可以使用new sun.misc.BASE64Encoder().encodeBuffer(byte[] b)方法
         * 保存为字符串
         *
         * @param str
         *            压缩前的文本
         * @return
         */
        public static final byte[] compress(Object obj) {
            if (obj == null)
                return null;
            byte[] compressed;
            ByteArrayOutputStream out = null;
            ZipOutputStream zout = null;
            try {
                out = new ByteArrayOutputStream();
                zout = new ZipOutputStream(out);
                zout.putNextEntry(new ZipEntry("0"));
                ByteArrayOutputStream out1 = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(out1);
                oos.writeObject(obj);
                byte[] bytes = out1.toByteArray();
                zout.write(bytes);
                zout.closeEntry();
                compressed = out.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                compressed = null;
            } finally {
                if (zout != null) {
                    try {
                        zout.close();
                    } catch (IOException e) {
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                    }
                }
            }
            return compressed;
        }

        /**
         * 将压缩后的 byte[] 数据解压缩
         *
         * @param compressed
         *            压缩后的 byte[] 数据
         * @return 解压后的字符串
         * @throws ClassNotFoundException
         */
        public static final Object decompress(byte[] compressed) {
            if (compressed == null)
                return null;
            ByteArrayOutputStream out = null;
            ByteArrayInputStream in = null;
            ZipInputStream zin = null;
            Object decompressed;
            try {
                out = new ByteArrayOutputStream();
                in = new ByteArrayInputStream(compressed);
                zin = new ZipInputStream(in);
                ZipEntry entry = zin.getNextEntry();
                byte[] buffer = new byte[1024];
                int offset = -1;
                while ((offset = zin.read(buffer)) != -1) {
                    out.write(buffer, 0, offset);
                }

                ByteArrayInputStream in1 = new ByteArrayInputStream(out.toByteArray());
                ObjectInputStream ois = new ObjectInputStream(in1);
                decompressed = ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
                decompressed = null;
            } finally {
                if (zin != null) {
                    try {
                        zin.close();
                    } catch (IOException e) {
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                    }
                }
            }
            return decompressed;
        }

}
