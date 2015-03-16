/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.util;

import cn.gooday.hrcn.common.bean.LogBean;
import cn.gooday.hrcn.common.event.events.DataReceivedEvent;
import cn.gooday.hrcn.common.util.LogUtil;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * [日志解析工具]
 *
 * @ProjectName: [hrcn]
 * @Author: [liushuo]
 * @CreateDate: [2015/3/3 11:10]
 * @Update:
 * @Version: [v1.0]
 */
public class AnalyzeUtil {

    public static LogBean analyzeLine(String logContent){
        logContent = logContent.replaceAll("\\s*","");
        if(!isLog(logContent)){
            return  null;
        }
        Pattern pattern =  Pattern.compile("\\[[a-z]+\\]\\d{4}-\\d{2}-\\d{4}:[0-9]{2}:[0-9]{2}\\([^\\(]*\\)\\([^\\(]*\\)\\([^\\(]*\\)\\([^\\(]*\\)\\[[a-z]+\\]");
        Matcher matcher = pattern.matcher(logContent);
        if(matcher.find()){
            logContent = matcher.group();
        }
        LogBean logBean = new LogBean();
        Pattern acPattern = Pattern.compile("\\[[a-z]+\\]");
        Pattern timePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{4}:[0-9]{2}:[0-9]{2}");
        Pattern contentPattern = Pattern.compile("\\([^\\(]*\\)");

        Matcher acMatcher = acPattern.matcher(logContent);
        Matcher timeMatcher = timePattern.matcher(logContent);
        Matcher contentMatcher = contentPattern.matcher(logContent);
        String actionCode = null;
        String actionTime = null;
        String object = null;
        String subject = null;
        String actionType = null;
        String level = null;

        if (acMatcher.find()){
            actionCode = acMatcher.group();
        }
        if(timeMatcher.find()){
            actionTime = timeMatcher.group();
        }
        if (contentMatcher.find()){
            object = contentMatcher.group();
        }
        if (contentMatcher.find()){
            subject = contentMatcher.group();
        }
        if (contentMatcher.find()){
            actionType = contentMatcher.group();
        }
        if (contentMatcher.find()){
            level = contentMatcher.group();
        }
        logBean.setActionCode(actionCode);
        logBean.setLevel(level);
        logBean.setMain(object);
        logBean.setTime(actionTime);
        logBean.setActionType(actionType);
        logBean.setSubject(subject);
        return logBean;
    }

    public static boolean isLog(String logContent){
        logContent = logContent.replaceAll("\\s*","");
//        Pattern pattern =  Pattern.compile("^\\[[a-z]+\\].*\\[[a-z]+\\]$");
        Pattern pattern =  Pattern.compile("\\[[a-z]+\\]\\d{4}-\\d{2}-\\d{4}:[0-9]{2}:[0-9]{2}\\([^\\(]*\\)\\([^\\(]*\\)\\([^\\(]*\\)\\([^\\(]*\\)\\[[a-z]+\\]");
        Matcher matcher = pattern.matcher(logContent);
        boolean flag = matcher.find();
//        return matcher.matches();
        return flag;
    }

    public static List<String> bufferToList(BufferedReader bufferedReader){
        List<String> logList = new ArrayList<String>();
        try{
            String lineLog;
            while ((lineLog = bufferedReader.readLine()) != null){
                logList.add(lineLog);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return logList;
    }

    public static void main(String[] a ){
//        String logContent = "[regiter]  2015-03-05 13:11:53  (13422222222,sure1024)  ()  ()  (INFO)  [register]";
//        AnalyzeUtil.analyzeLine(logContent);
//        System.out.print(AnalyzeUtil.isLog(logContent));


//        LogAnalyzer logAnalyzer = new LogAnalyzer(new TitleAnalyzer());
//        DataReceivedEvent event = new DataReceivedEvent();
//        String logContent = "[regiter]  2015-03-05 13:11:53  (13422222222,sure1024)  ()  ()  (INFO)  [register]" +
//                "\n[regiter]  2015-03-05 13:11:53  (13422222222,sure1024)  ()  ()  (INFO)  [register]";
//        event.setEventObject("logContent");
//        logAnalyzer.analyze(event);
    }

}
