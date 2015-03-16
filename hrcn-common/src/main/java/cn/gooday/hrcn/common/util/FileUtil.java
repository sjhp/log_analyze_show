/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.util;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * [文件操作的工具类]
 *
 * @ProjectName: [hrcn]
 * @Author: [lixu]
 * @CreateDate: [2015/2/10 22:30]
 * @Update: [说明本次修改内容] BY[tophawk][2015/2/10]
 * @Version: [v1.0]
 */
public class FileUtil {
    /**
     * 缓存大小
     */
    private static final int BUFFER_SIZE = 1024 * 1024;

    private FileUtil() {
    }


    /**
     * 获取路径下特定文件类型的文件列表
     *
     * @param filePath 文件目录
     * @param fileType 文件类型
     * @return 没有文件返回null
     */
    public static List<File> getFilesByDirection(String filePath, final String fileType) {
        File[] e = (new File(filePath)).listFiles(new FileFilter() {
            public boolean accept(File file) {
                boolean result = file.isFile();
                if (fileType != null && !fileType.equals("")) {
                    if (file.getName().endsWith(fileType)) {
                        return result;
                    }
                    return false;
                } else {
                    return result;
                }

            }
        });
        if (e != null && e.length > 0) {
            return Arrays.asList(e);
        }
        return null;
    }

    /**
     * 读取文件，以指定编码方式返回文件内容
     *
     * @param path 文件的全路径
     * @param enc  编码
     * @return
     * @throws IOException
     */
    public static final String getFileContent(String path, String enc)
            throws IOException {
        StringBuffer bufLine = new StringBuffer();
        try {
            File f = new File(path);
            if (f.exists()) {
                InputStreamReader read = new InputStreamReader(new FileInputStream(f), enc);
                BufferedReader br = new BufferedReader(read);
                String line = br.readLine();
                while (line != null) {
                    bufLine.append(line).append("\r\n");
                    line = br.readLine();
                }
                br.close();
                read.close();
                br = null;
                read = null;
            }

        } catch (IOException e) {
            throw e;
        }
        return bufLine.toString();
    }


    /**
     * 创建文件
     *
     * @param data         文件内容
     * @param fileLocation 文件放置的地址
     * @param fileName     文件名称
     * @param isOverFile 判断是否在覆盖文件 true--支持在存在的文件中写入 /false--不支持在存在的文件写入
     * @param isOverContent 判断文件内容是覆盖还是追加写入  true -- 覆盖写入 / false 在文件中追加写入
     * @return
     */
    public static boolean createFile(String data, String fileLocation, String fileName , boolean isOverFile , boolean isOverContent) {
        boolean pathExist = validyPath(fileLocation);
        String totalFilePath = StringUtil.join(fileLocation, File.separator, fileName);
        if (pathExist) {
            File file = new File(totalFilePath);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    LogUtil.err("the fileName:{} create error ,the reason is {}", totalFilePath, e.getMessage());
                    throw new RuntimeException(StringUtil.join("The file [", totalFilePath, "] create error , the reason is:", e.getMessage()));
                }
            } else {
                if(!isOverFile) {
                    LogUtil.err("The file {} is already exists", totalFilePath);
                    throw new RuntimeException(StringUtil.join("The file [", totalFilePath, "] is already exists ,cannot cover the file"));
                }
            }
            BufferedWriter bfw = null;
            try {
                bfw = new BufferedWriter(new FileWriter(file,isOverContent));
                bfw.write(data);
            } catch (IOException e) {
                LogUtil.err("the file:{} write data error , the reason is ", file.getName(), e.getMessage());
                throw new RuntimeException(StringUtil.join("The file[", file.getName(), "] write data  error ,the error message :", e.getMessage()));
            } finally {
                try {
                    bfw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    bfw = null;
                }
            }
        }
        return true;
    }

    /**
     * 按照文件全路径进行删除文件操作
     *
     * @param filePath
     * @return true -- 删除文件成功/false -- 删除文件失败
     */
    public static boolean deleteFileByPath(String filePath) {
        File file = new File(filePath);
        boolean isDelete = true;
        if (file.exists()) {
            isDelete = file.delete();
        }
        return isDelete;
    }


    /**
     * 校验路径，没有路径则创建路径
     *
     * @param path
     * @return
     */
    public static final boolean validyPath(String path) {
        String[] arraypath = path.split("/");
        String tmppath = "";
        for (int i = 0; i < arraypath.length; i++) {
            tmppath += "/" + arraypath[i];
            File d = new File(tmppath);
            if (!d.exists()) {
                if (!d.mkdir()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 读取文件内容，转换成String对象返回
     *
     * @param file
     * @return
     */
    public static String getFileContents(File file) {
        StringBuffer lineContentBuff = new StringBuffer();
        if (file != null && file.exists() && file.isFile()) {
            InputStreamReader reader = null;
            BufferedReader bufferReader = null;
            try {
                reader = new InputStreamReader(new FileInputStream(file));
                bufferReader = new BufferedReader(reader);
                String lineContent = bufferReader.readLine();
                while (lineContent != null) {
                    if (!lineContent.trim().equals("")) {
                        lineContentBuff.append(lineContent).append("\r\n");
                    }
                    lineContent = bufferReader.readLine();
                }
            } catch (Exception e) {
                LogUtil.err("Read the file:{} error , the error reason is:{}", file.getName(), e.getMessage());
                throw new RuntimeException(StringUtil.join("Read the file [", file.getName(), "] error , the reason is ", e.getMessage()));
            } finally {
                try {
                    bufferReader.close();
                    reader.close();
                } catch (IOException e) {
                    LogUtil.err("the read stream cannot close , the reason is {}", e.getMessage());
                    throw new RuntimeException(StringUtil.join("the read stream cannot close , the reason is ", e.getMessage()));
                } finally {
                    bufferReader = null;
                    reader = null;
                }
            }
        }
        return lineContentBuff.toString();

    }

    public static File getSigleFile(String fileTotalPath) {
        File file = new File(fileTotalPath);
        if (file.exists() && file.isFile()) {
            return file;
        }
        return null;
    }

    public static boolean renameFile(File targetFile, String fileTotalPath) {
        try {
            File file = new File(fileTotalPath);
            if (file.exists()) {
                LogUtil.err("target file :{} is exists, please check the data", fileTotalPath);
                return false;
            }
            targetFile.renameTo(file);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.err("the target file:{} cannot remove to the file :{}", targetFile.getName(), fileTotalPath, e.getMessage());
            throw e;
        }
    }

    public static String readFileContext(File file) {
        FileChannel fis = null;
        StringBuffer strBuffer = new StringBuffer();
        try {
            fis = new RandomAccessFile(file, "r").getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            byte[] bytes = new byte[BUFFER_SIZE];
            String tempString = null;
            while (fis.read(buffer) != -1) {
                int rSize = buffer.position();
                buffer.rewind();
                buffer.get(bytes);
                buffer.clear();
                tempString = new String(bytes, 0, rSize);
                strBuffer.append(tempString);
            }
            LogUtil.info("read the file {} success", file.getName());
        } catch (FileNotFoundException e) {
            LogUtil.err("the file:{} is not exists", e.getMessage());
            throw new RuntimeException(StringUtil.join("the file [", file.getName(), "] read error ,the error reason is ", e.getMessage()));
        } catch (Exception e) {
            LogUtil.err("read the file:{} error ,the reason is:{}", e.getMessage());
            throw new RuntimeException(StringUtil.join("read the file [", file.getName(), "] error ,the reason is ", e.getMessage()));
        } finally {
            try {
                fis.close();
                fis = null;
                return strBuffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(StringUtil.join("read the file [", file.getName(), "] error ,the reason is ", e.getMessage()));
            }

        }
    }

    /**
     * 按照指定指针和指定行读取文件内容
     * @param row  要开始读取的行数
     * @param file  要开始读取的文件
     * @return
     */
    /**
     * 按照指定指针，指定行数，编码方式和开始读取的位置
     * @param row  指定读取行数
     * @param file 要读取的文件对象
     * @param encoder 编码方式
     * @param map 放入读取文件的细节信息，为了以后进行定位
     * @return
     */
    public static String readFileByPosition(int row ,File file ,String encoder,Map map) {
        RandomAccessFile logFile = null;
        StringBuffer list = new StringBuffer();
        int readLine = 0;
        try {
            //以只读方式打开日志文件
            logFile = new RandomAccessFile(file, "rw");
            //计算要读取的行号，从末行开始读取，所以末行行号为0
            //获取文件大小
            long len = logFile.length();
            long position =  FileUtil.readFilePointer(file.getAbsolutePath());
            map.put("start_position",position);
            if(position >= len){
                return null;
            }
            logFile.seek(position);
            if(len > 0) {
                String context = null;
                while((context = logFile.readLine()) != null && position <= len){
                    readLine++;
                    list.append(new String(context.getBytes("8859_1"),encoder)).append("\r\n");
                    if(readLine == row){
                        break;
                    }
                }
            }
            try {
                long endFilePosition = logFile.getFilePointer();
                writerFilePointer(file.getAbsolutePath(),endFilePosition);
                map.put("end_position",endFilePosition);
                map.put("read_row",readLine);
                if(logFile != null) logFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //读取文件首行
            if(list.length() == 0) {  //没有读取到任何数据，表示已经加载过了所有内容
               LogUtil.info("the file {}  has readed completed!" , file.getName());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogUtil.info("It is not exists the file ;{} " , file.getName());
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.err("the file {}  read failed! " + file.getName());
        } finally {
            try {
                logFile.close();
                logFile = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
     return list.toString();
    }

    /**
     * 获取指定文件读取的指针，为了进行接下来文件的读取
     * @param fileContext  文件中要读取的内容
     * @return
     */
    public static synchronized long readFilePointer(String fileContext) {
        InputStream in = null;
        try {
            String fileLocation = RtUtil.getConfigService().getString("cache.pointer.file.path");
            boolean pathExist = validyPath(fileLocation);
            if(!pathExist){
               throw new RuntimeException("指针文件路径解析有异常");
            }
            File file =   new File(fileLocation, RtUtil.getConfigService().getString("cache.pointer.file.name"));
            if(!file.exists()) {
                   if(!file.createNewFile()){
                       throw new RuntimeException("指针文件创建不成功");
                   }
            }
            Properties proper = new Properties();
            in = new BufferedInputStream(new FileInputStream(file));
            proper.load(in);
            String value = proper.getProperty(fileContext);
            if(value == null || value.equals("")){
                return 0;
            }else{
                return Long.parseLong(value);
            }
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                in = null;
            }
        }
    }

    /**
     * 获取指定文件读取的指针，为了进行接下来文件的读取
     * @param fileContext  文件中要读取的内容
     * @param pointer      文件中指针值
     * @return
     */
    public static synchronized void writerFilePointer(String fileContext , long pointer){
           OutputStream fos = null;
           try{
               String fileLocation = RtUtil.getConfigService().getString("cache.pointer.file.path");
               boolean pathExist = validyPath(fileLocation);
               if(!pathExist){
                   throw new RuntimeException("指针文件路径解析有异常");
               }
               File file =   new File(fileLocation, RtUtil.getConfigService().getString("cache.pointer.file.name"));
               if(!file.exists()) {
                   if(!file.createNewFile()){
                       throw new RuntimeException("指针文件创建不成功");
                   }
               }
               String profilePath = StringUtil.join(RtUtil.getConfigService().getString("cache.pointer.file.path"),File.separator, RtUtil.getConfigService().getString("cache.pointer.file.name"));
               Properties props = new Properties();
               props.load(new FileInputStream(profilePath));
               fos = new FileOutputStream(profilePath);
               props.setProperty(fileContext,Long.toString(pointer) );

               props.store(fos, StringUtil.join("updata " , fileContext , " value =" , Long.toString(pointer)));

           }catch (Exception e){
               e.printStackTrace();
           }finally {
               if( fos != null){
                   try {
                       fos.close();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
                   fos = null;
               }
           }
    }

    public static  String getFilecharset(File sourceFile) {
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1) {
                return charset; //文件编码为 ANSI
            } else if (first3Bytes[0] == (byte) 0xFF
                    && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE"; //文件编码为 Unicode
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE
                    && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE"; //文件编码为 Unicode big endian
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF
                    && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8"; //文件编码为 UTF-8
                checked = true;
            }
            bis.reset();
            if (!checked) {
                int loc = 0;
                while ((read = bis.read()) != -1) {
                    loc++;
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) // 双字节 (0xC0 - 0xDF)
                            // (0x80
                            // - 0xBF),也可能在GB编码内
                            continue;
                        else
                            break;
                    } else if (0xE0 <= read && read <= 0xEF) {// 也有可能出错，但是几率较小
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return charset;
    }

}