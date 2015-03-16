/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.collect.thread;

import cn.gooday.hrcn.common.bean.BaseBean;

import cn.gooday.hrcn.common.config.ConfigService;
import cn.gooday.hrcn.common.constant.Constants;
import cn.gooday.hrcn.common.util.FileUtil;
import cn.gooday.hrcn.common.util.LogUtil;
import cn.gooday.hrcn.common.util.RtUtil;
import cn.gooday.hrcn.common.util.StringUtil;
import sun.misc.BASE64Encoder;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * [负责文件日志信息主动收集]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/2/11 0:06]
 * @Update: [说明本次修改内容] BY[tophawk][2015/2/11]
 * @Version: [v1.0]
 */
public class FileLogCollectThread extends CollectThread {
    private Thread[] pool = null;

    @Override
    public void run() {
        boolean threadStatus = true;
        while (true) {
            try {
                ConfigService cs = RtUtil.getConfigService();
                //在配置文件中配置读取的数据源路径，几个数据源用几个线程分别读取数据
                //每次读取数据的行数
                String readLine = cs.getString("sigle.file.read.line");
                //读取完以后等待时间
                String readInterval = cs.getString("cache.read.interval");
                int row = Integer.parseInt(readLine);
                int waitTime = Integer.parseInt(readInterval);
                //配置要读取的数据源路径
                String[] filePaths = cs.getString("cache.log.collect.path").split(",");
                int size = filePaths.length;
                if(size > 0) {
                    ExecutorService executor  = Executors.newFixedThreadPool(size);
                    pool = new Thread[size];
                    for (int i = 0 ; i < size; i++) {
                        pool[i] = new Thread(new ReadLogFileRunnable(filePaths[i].trim(), row, waitTime), StringUtil.join("Thread-", Integer.toString(i), "-readFile"));
                        executor.submit(pool[i]);
                    }
                    while (threadStatus) {
                        threadStatus = false;
                        for (Thread t : pool) {
                            if (t.isAlive()) {
                                threadStatus = true;
                            }
                        }

                    }
                    executor.shutdown();
                }
                Thread.sleep(10000);
            } catch (Exception ex) {
                ex.printStackTrace();
                LogUtil.sendErr(ex);
            }
        }
    }

    class ReadLogFileRunnable implements Runnable {
        private String filePath;
        private String tomcatName;
        private String ip;
        private int row;
        private int waitTime;
        private Map<String,Object>  transferInfo = new HashMap<String,Object>();

        public ReadLogFileRunnable(String filePath, int row, int waitTime) {
            if(filePath.contains("&&&")) {
                this.filePath = filePath.split("&&&")[0];
                this.tomcatName = filePath.split("&&&")[1];
            }else{
                LogUtil.info("config properties not includ  data source name!");
                this.filePath = filePath;
                this.tomcatName = "";
            }
            ip = RtUtil.getConfigService().getString("local.ip.address");
            this.row = row;
            this.waitTime = waitTime;
        }

        @Override
        public void run() {
            try {
                //获取路径指定路径下的日志文件
                File file = new File(filePath);
                if (!file.exists() || !file.isFile()) {
                    LogUtil.err("the file {} not exists， please check the file data", filePath);
                    return;
                }
                    //获取文件编码格式，为了解决中文乱码问题
                    String encoder = FileUtil.getFilecharset(file);
                    while (true ) {
                        Thread.sleep(waitTime);
                        String value = FileUtil.readFileByPosition(row, file, encoder,transferInfo);
                        if (value != null) {
                            BaseBean bean = new BaseBean();
                            bean.setContent(value);
                            bean.setTomcatName(tomcatName);
                            bean.setServerIp(ip);
                            //发送处理
                            boolean collect = sendCollectData(bean);
                                //失败重试
                                if(!collect){
                                int retry = RtUtil.getConfigService().getInt("collect.retry");
                                if (retry == 0) retry = Constants.SYSTEM_FAIL_RETRY;
                                int count = retry;
                                while(count-->0 ||(collect=sendCollectData(bean))){
                                    try {
                                        Thread.sleep(Constants.SYSTEM_FAIL_RETRY_INTERVAL);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if(count <=0 && (!collect)){
                                    //将失败数据放入到存放错误文件的路径下面，便于之后进行数据分析和传输
                                    LogUtil.err("all of {} retries of the data {} is fail,push to errorFile!",retry,file.getName());
                                    writerErrorFile(file.getName(),bean);
                                }
                            }
                            LogUtil.info("the current thread:{} read the file:{} success ,has read file from  {} to {}  the line number is {}", Thread.currentThread().getName(), file.getName(),transferInfo.get("start_position") ,transferInfo.get("end_position")   ,transferInfo.get("read_row"));
                        } else
                            break;
                    }
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    throw e;
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
    /**
     * 发送收集的数据，截获异常信息
     * @param bean
     * @return
     */
    private boolean sendCollectData(BaseBean bean){
        try{
            collector.collect(bean);
        }catch(Exception e){
            LogUtil.err("collect the data is error, the error info is :{}" ,e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 收集数据处理不成功，将把数据存入的错误文件中，便于以后进行分析
     * @param fileName
     * @param bean
     */
    private void writerErrorFile(String fileName , BaseBean bean){
        try{
            String errorFilePath = RtUtil.getConfigService().getString("collect.fail.path");
            StringBuffer newFileName = new StringBuffer();
            newFileName.append(fileName.substring(0, fileName.indexOf(".")));
            String prefix = StringUtil.join(bean.getServerIp() , "&" , bean.getTomcatName());
            String encoderStr = (new BASE64Encoder()).encode(prefix.getBytes());
            newFileName.append("_").append(encoderStr).append(fileName.substring(fileName.indexOf(".")));
            FileUtil.createFile(bean.getContent(),errorFilePath,newFileName.toString(),true,true);
        }catch(Exception e){
             e.printStackTrace();
             LogUtil.err("the error log info store in file error ,the error info :{}",bean.toString());
            return;
        }
        LogUtil.err("error log info store in file success");
    }


}
