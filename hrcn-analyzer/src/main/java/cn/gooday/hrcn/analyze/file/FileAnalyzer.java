/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.analyze.file;

import cn.gooday.hrcn.common.bean.LogBean;
import cn.gooday.hrcn.common.util.LogUtil;
import cn.gooday.hrcn.common.util.MongoDBManager;
import cn.gooday.hrcn.util.AnalyzeUtil;

import java.util.List;
import java.util.concurrent.RecursiveTask;

/**
 * [日志解析]
 *
 * @ProjectName: [hrcn]
 * @Author: [liushuo]
 * @CreateDate: [2015/3/3 11:10]
 * @Update:
 * @Version: [v1.0]
 */
public class FileAnalyzer extends RecursiveTask<Boolean> {

    private List<String> logList;
    private int lineNum;
    private String tableName;

    public FileAnalyzer(List<String> logList,int lineNum,String tableName){
        this.logList = logList;
        this.lineNum = lineNum;
        this.tableName = tableName;
    }

    @Override
    public Boolean compute(){
        boolean flag = false;

        if(lineNum < logList.size()){

            String logContent = logList.get(lineNum);
            LogBean logBean = AnalyzeUtil.analyzeLine(logContent);
            if(null == logBean){
                FileAnalyzer fileAnalyzer = new FileAnalyzer(logList,lineNum+1,tableName);
                fileAnalyzer.fork();
                flag = true;
                flag = flag && fileAnalyzer.join();
            }else {
                MongoDBManager.mongoTemplate.save(logBean,tableName);
                FileAnalyzer fileAnalyzer = new FileAnalyzer(logList,lineNum+1,tableName);
                fileAnalyzer.fork();
                flag = true;
                flag = flag && fileAnalyzer.join();
            }
        }else {
            flag = true;
        }
        LogUtil.debug("line number is "+lineNum+",analyze success");
        return flag;
    }
}