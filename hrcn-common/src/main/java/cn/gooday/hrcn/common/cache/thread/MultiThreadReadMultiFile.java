/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.cache.thread;
import cn.gooday.hrcn.common.bean.BaseBean;
import cn.gooday.hrcn.common.constant.EnumEventType;
import cn.gooday.hrcn.common.event.Event;
import cn.gooday.hrcn.common.event.events.CollectFinishedEvent;
import cn.gooday.hrcn.common.event.events.DataCollectedEvent;
import cn.gooday.hrcn.common.event.events.DataReceivedEvent;
import cn.gooday.hrcn.common.util.FileUtil;
import cn.gooday.hrcn.common.util.LogUtil;
import sun.misc.BASE64Decoder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * [多线程操作读取目录下多个文件]
 * @ProjectName: [hrcn]
 * @Author: [lixu]
 * @CreateDate: [2015/3/1 22:46]
 * @Update: [说明本次修改内容] BY[lixu][2015/3/4]
 * @Version: [v1.0]
 */
public class MultiThreadReadMultiFile {
    private final File POISON = new File("");
    private final BlockingQueue<File> files = new LinkedBlockingQueue<File>(10);
    private final ConcurrentLinkedQueue<Event<BaseBean>> fileInfos = new ConcurrentLinkedQueue<Event<BaseBean>>();
    private Thread[] pool = null;
    /**
     * 判断是否运行结束
     */
    private volatile boolean running = false;

    public MultiThreadReadMultiFile(int poolSize) {
        pool = new Thread[poolSize];
        FileWorker worker = new FileWorker();
        for (int i = 0, size = pool.length; i < size; i++) {
            pool[i] = new Thread(worker, "The Read  Thread-" + (i + 1));
            pool[i].start();
        }
        running = true;
    }

    /**
     * 将文件列表传入
     * @param files
     */
    public void addFilesIgnoreInterrupted(List<File> files) {
        for (File file : files) {
            try {
                this.files.put(file);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * 关闭文件读取操作
     */
    public void shutdown() {
        try {
            if (running) {
                running = false;
                files.put(POISON);
            }
        } catch (InterruptedException e) {
        }
    }

    /**
     * 等待
     */
    public void waiting() {
        if (running || !files.contains(POISON)) {
            LogUtil.info("Thread status is {} , files pool size is {}", running,files.size());
            throw new IllegalStateException("You must call shutdown() function before.");
        }
        for (Thread t : pool) {
            try {
                // 等待线程结束
                t.join();
            } catch (InterruptedException e) {
                LogUtil.err(e.getMessage());
            }
        }
    }

    public Queue<Event<BaseBean>> getFileInfos() {
        return fileInfos;
    }

    /**
     * 进行文件读取的核心操作
     */
    private class FileWorker implements Runnable {
        @Override
        public void run() {
            File file = null;
            try {
                while ((file = files.take()) != POISON) {
                    try {
                        doWork(file);
                    } catch (Exception e) {
                        onException(e, file);
                    }
                }
                files.put(POISON);
            } catch (InterruptedException e) {
                LogUtil.err(" read the file:{} error, the reason :{}",file.getName(), e.getMessage());
            }
        }

        private void onException(Exception e, File file) {
            LogUtil.err("read the file {} exception , the exception reason:{}",file.getName() , e.getMessage());
        }

        private void doWork(File file) {
            try {
                Event<BaseBean> event = null;
                Map map = new HashMap();
                String fileName = file.getName();
                fileName = fileName.substring(0 , fileName.indexOf("."));
                int splitLength = fileName.indexOf("_",0);
                int endLength = fileName.lastIndexOf("_");
                EnumEventType eventType = EnumEventType.valueOf(fileName.substring(splitLength + 1 , endLength));
                String encoderPrefix = fileName.substring(endLength+1);
                switch(eventType){
                    case DATA_COLLECTED_EVENT: event = new DataCollectedEvent();break;
                    case COLLECT_FINISHED_EVENT: event = new CollectFinishedEvent(); break;
                    case DATA_RECEIVED_EVENT: event = new DataReceivedEvent();break;
                    default: LogUtil.err("the file[{}] cannot be readed , the reason is the eventType {} cannot be resolved",file.getName() ,eventType);return;
                }
                String decoderValue = new String((new BASE64Decoder()).decodeBuffer(encoderPrefix));
                BaseBean bean = new BaseBean();
                bean.setServerIp(decoderValue.split("&")[0]);
                bean.setTomcatName(decoderValue.split("&")[1]);
                bean.setContent(FileUtil.getFileContent(file.getAbsolutePath(), FileUtil.getFilecharset(file)));
                event.setEventNo(fileName.substring(0, splitLength));
                event.setEventObject(bean);
                fileInfos.add(event);
                LogUtil.info("the current thread:{} read the file:{} success", Thread.currentThread().getName(), file.getName());
            } catch (Exception e) {
                LogUtil.err("the thread read the file :{} error , the error reason is :{}", file.getName(), e.getMessage());
                try {
                    throw e;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}