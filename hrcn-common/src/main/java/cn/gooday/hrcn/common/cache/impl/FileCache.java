/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.cache.impl;

import cn.gooday.hrcn.common.bean.BaseBean;
import cn.gooday.hrcn.common.cache.ICache;
import cn.gooday.hrcn.common.cache.thread.MultiThreadReadMultiFile;
import cn.gooday.hrcn.common.config.ConfigService;
import cn.gooday.hrcn.common.constant.EnumEventType;
import cn.gooday.hrcn.common.event.Event;
import cn.gooday.hrcn.common.event.events.CollectFinishedEvent;
import cn.gooday.hrcn.common.event.events.DataCollectedEvent;
import cn.gooday.hrcn.common.event.events.DataReceivedEvent;
import cn.gooday.hrcn.common.exception.CacheException;
import cn.gooday.hrcn.common.util.*;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * [文件缓存接口实现类]
 * 进行缓存数据到文件的读取和写入，修改
 * @ProjectName: [hrcn]
 * @Author: [lixu]
 * @CreateDate: [2015/3/1 22:46]
 * @Update: [说明本次修改内容] BY[lixu][2015/3/4]
 * @Version: [v1.0]
 */
public class FileCache implements ICache<BaseBean> {
    private String cacheFilePath;
    private String cacheFileType;

    public FileCache(){
        ConfigService  cs = RtUtil.getConfigService();
        cacheFilePath = cs.getString("cache.file.path");
        cacheFileType = cs.getString("cache.file.type");
    }

    /**
     * 添加缓存数据到文件中
     * @param object
     * @return
     * @throws CacheException
     */
    @Override
    public String add(Event<BaseBean> object) throws CacheException {
        try {
            if(StringUtil.isBlank(object.getEventNo())){
                object.setEventNo(GuidGenerator.getGUID());
            }
            BaseBean baseBean = object.getEventObject();

            if(StringUtil.isBlank(baseBean.getContent())){
                LogUtil.err("create cache file error , the reason : the cache :{}  data is null",StringUtil.join(object.getEventType().name(),":",object.getEventNo()));
                throw new CacheException(StringUtil.join("event object[",object.getEventType().name(),":",object.getEventNo(),"]  create cache file error, the reason  is event data is null"));
            }
            String cacheFileName = getCacheFileName(object.getEventNo(), object.getEventType() , baseBean);
            String fileName = StringUtil.join(cacheFileName, ".", cacheFileType);
            boolean result = FileUtil.createFile(baseBean.getContent() ,cacheFilePath ,fileName ,false,false);
            if (!result) {
                LogUtil.err("Event object:{} create file cache failed!",StringUtil.join(object.getEventType().name(),":",object.getEventNo()));
                throw new CacheException(StringUtil.join("Event object[",object.getEventType().name(),":",object.getEventNo(),"] create file cache failed!"));
            }
            LogUtil.info("Event object[;{} create file cache success!", StringUtil.join(object.getEventType().name(), ":", object.getEventNo()));
            return object.getEventNo();
        }catch(Exception e){
            LogUtil.err("Event object:{} create file cache error, the reason is:{} ",StringUtil.join(object.getEventType().name(),":",object.getEventNo()),e.getMessage());
            throw new CacheException(StringUtil.join("Event object[",object.getEventType().name(),":",object.getEventNo(),"] create file cache error!"));
        }
    }

    @Override
    public void remove(Event<BaseBean> object) {
        boolean isDelete = true;
        try {
            BaseBean baseBean = object.getEventObject();
            String cacheFileName = null;
            if(baseBean != null) {
                cacheFileName = getCacheFileName(object.getEventNo(), object.getEventType(), baseBean);
                String fileTotalPath = StringUtil.join(cacheFilePath, File.separator, cacheFileName, ".", cacheFileType);
                isDelete = FileUtil.deleteFileByPath(fileTotalPath);
            }else{
                List<File> files = FileUtil.getFilesByDirection(cacheFilePath,cacheFileType);
                if(files == null || files.size() == 0){
                    LogUtil.info("cache file location:{} include no cache file",cacheFilePath);
                    return;
                }
                for(File file : files){
                    if(file.getName().contains(StringUtil.join(object.getEventNo(),"_",object.getEventType().name()))){
                        isDelete = FileUtil.deleteFileByPath(file.getAbsolutePath());
                        break;
                    }
                }
            }
            if (!isDelete) {
                LogUtil.err("Event object;{} remove the cache file failed!", cacheFileName);
                throw new CacheException(StringUtil.join("Event object[",cacheFileName,"] remove the cache file failed!"));
            }
            LogUtil.info("Event object;{} remove the cache file success!", cacheFileName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CacheException(StringUtil.join("Event Object[", object.getEventType().name(), ":", object.getEventNo(), "] remove the cache file failed , the reason ::", e.getMessage()));
        }
    }

    @Override
    public Event<BaseBean> get(String cacheNo , EnumEventType eventType , BaseBean baseBean) {
        String fileTotalPath = StringUtil.join(cacheFilePath, File.separator, getCacheFileName(cacheNo, eventType , baseBean), ".", cacheFileType);
        File file = FileUtil.getSigleFile(fileTotalPath);
        if (file == null) {
            LogUtil.info("Event object:{} cannot find the cache file", fileTotalPath);
            return null;
        }
        String data = null;
        try {
            data = FileUtil.getFileContent(file.getAbsolutePath(), FileUtil.getFilecharset(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        BaseBean bean = new BaseBean();
        bean.setContent(data);
        bean.setServerIp(baseBean.getServerIp());
        bean.setTomcatName(baseBean.getTomcatName());
        Event<BaseBean> event = getEventObject(cacheNo , eventType ,bean );
        return event;
    }

    @Override
    public List<Event<BaseBean>> getAll() {
        List<Event<BaseBean>>  events = new ArrayList<Event<BaseBean>>();
        List<File> files = FileUtil.getFilesByDirection(cacheFilePath,cacheFileType);
        if(files == null || files.size() == 0){
            LogUtil.info("cache file location:{} include no cache file",cacheFilePath);
            return events;
        }
        //多线程读取文件
        int size = Runtime.getRuntime().availableProcessors();
        int fileSize = files.size();
        if(fileSize > 0) {
            int threadSize = fileSize < 20 ? size : fileSize / 10;
            MultiThreadReadMultiFile instance = new MultiThreadReadMultiFile(threadSize);
            instance.addFilesIgnoreInterrupted(files);
            instance.shutdown();
            instance.waiting();
            Queue<Event<BaseBean>> queue = instance.getFileInfos();
            while (queue.size() != 0) {
                events.add(queue.poll());
            }
        }
        return events;
    }

    @Override
    public boolean rename(String eventNo, EnumEventType targetEventType, EnumEventType newEventType , String prefix) {
        String fileTotalPath = StringUtil.join(cacheFilePath, File.separator, getCacheFileName(eventNo, targetEventType , prefix), ".", cacheFileType);
        String newFilePath = StringUtil.join(cacheFilePath, File.separator, getCacheFileName(eventNo, newEventType , prefix), ".", cacheFileType);
        try {
            File file = FileUtil.getSigleFile(fileTotalPath);
            if (file == null) {
                LogUtil.err("file location:{} include no cache file", fileTotalPath);
                throw new CacheException(StringUtil.join("file location [", fileTotalPath, "] include no cache file!"));
            } else {
                return FileUtil.renameFile(file , newFilePath);
            }
        }catch (Exception e){
            throw new CacheException(StringUtil.join("the cache file[", fileTotalPath, "cannot not remove to ",newFilePath,"], the reason is :",e.getMessage()));
        }
    }

    /**
     * 获取文件名称
     * @param cacheNo
     * @param eventType
     * @param baseBean 后缀名称
     * @return
     */
    private String getCacheFileName(String cacheNo, EnumEventType eventType ,BaseBean baseBean) {
        String prefix = StringUtil.join(baseBean.getServerIp() , "&" , baseBean.getTomcatName());
        String encoderStr = (new BASE64Encoder()).encode(prefix.getBytes());
        return getCacheFileName(cacheNo,eventType,encoderStr);
    }

    private String getCacheFileName(String cacheNo, EnumEventType eventType ,String prefix){
        return StringUtil.join(cacheNo, "_", eventType.name() , "_" ,prefix);
    }

    /**
     * 将文件缓存数据封装成事件对象
     * @param eventType
     * @param cacheNo
     * @param bean
     * @return
     */
    private Event<BaseBean> getEventObject(String cacheNo ,EnumEventType eventType , BaseBean bean){
        Event<BaseBean> event  = null;
        switch(eventType){
            case DATA_COLLECTED_EVENT:   event = new DataCollectedEvent();break;
            case COLLECT_FINISHED_EVENT: event = new CollectFinishedEvent();break;
            case DATA_RECEIVED_EVENT:    event = new DataReceivedEvent(); break;
            default: return null;
        }
        event.setEventNo(cacheNo);
        event.setEventObject(bean);
        return event;
    }

}