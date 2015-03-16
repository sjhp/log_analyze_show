/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.analyze;

import cn.gooday.hrcn.analyze.title.TitleAnalyzer;
import cn.gooday.hrcn.common.bean.BaseBean;
import cn.gooday.hrcn.common.constant.Constants;
import cn.gooday.hrcn.common.constant.EnumEventType;
import cn.gooday.hrcn.common.event.Event;
import cn.gooday.hrcn.common.event.events.DataReceivedEvent;
import cn.gooday.hrcn.common.util.LogUtil;
import cn.gooday.hrcn.common.util.MongoDBManager;
import cn.gooday.hrcn.common.util.StringUtil;
import cn.gooday.hrcn.util.AnalyzeUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

/**
 * [数据解析入口]
 *
 * @ProjectName: [hrcn]
 * @Author: [liushuo]
 * @CreateDate: [2015/3/3 11:10]
 * @Update:
 * @Version: [v1.0]
 */
public class LogAnalyzer {

    private static  LogAnalyzer logAnalyzer = null;

    private TitleAnalyzer titleAnalyzer;

    private LogAnalyzer(TitleAnalyzer titleAnalyzer){
        LogUtil.debug("create LogAnalyzer!");
        this.titleAnalyzer = titleAnalyzer;
    }

    /**
     * 创建解析对象--单例模式
     * @return
     */
    public static LogAnalyzer getInstance(){
        if(logAnalyzer == null){
            logAnalyzer = new LogAnalyzer(new TitleAnalyzer());
        }
        return logAnalyzer;
    }

    public boolean analyze(Event<BaseBean> event){
        BaseBean baseBean = event.getEventObject();
        if(EnumEventType.STATUS_CHANGE_EVENT.equals(event.getEventType())){
            LogUtil.debug("analyze client status !");
            MongoDBManager.saveBsonStr(baseBean.getContent(),Constants.CLIENT_RUNTIME_INFO);
            return  true;
        }
        String tableName = baseBean.getServerIp()+baseBean.getTomcatName();
        if(StringUtil.isBlank(tableName)){
            return false;
        }
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(baseBean.getContent().getBytes(Charset.forName("utf8"))),Charset.forName("utf8")));
        List<String> logList = AnalyzeUtil.bufferToList(bufferedReader);
        LogUtil.debug("analyze file, size:"+logList.size());
        for(String logContent:logList){
            BaseBean baseBeans = new BaseBean();
            baseBeans.setServerIp(baseBean.getServerIp());
            baseBeans.setTomcatName(baseBean.getTomcatName());
            baseBeans.setContent(logContent);
            MongoDBManager.mongoTemplate.save(baseBeans,tableName+ Constants.BASE_LOG_ALL);
        }
        return titleAnalyzer.analyze(logList,tableName+Constants.BASE_LOG_ANALYZE);
    }

    public static void main(String[] a){
        LogAnalyzer logAnalyzer = new LogAnalyzer(new TitleAnalyzer());
        DataReceivedEvent event = new DataReceivedEvent();

//        String logContent = "[regiter]  2015-03-05 13:11:53  (13422222222,sure1024)  ()  ()  (INFO)  [register]" +
//                "\n[regiter]  2015-03-05 13:11:53  (13422222222,sure2048)  ()  ()  (INFO)  [register]";
        String logContent = "14:21:39,936 INFO OTHER (263) - [login]  2015-03-11 14:21:39  (yzab19880000)  ()  ()  (INFO)  [login]";
        BaseBean baseBean = new BaseBean();
        baseBean.setServerIp("192.168.0.1");
        baseBean.setTomcatName("buyer");
        baseBean.setContent(logContent);
        event.setEventObject(baseBean);
        logAnalyzer.analyze(event);
    }



}