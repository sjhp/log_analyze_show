/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.analyze.title;

import cn.gooday.hrcn.analyze.file.FileAnalyzer;
import cn.gooday.hrcn.common.util.LogUtil;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

/**
 * [并发处理]
 *
 * @ProjectName: [hrcn]
 * @Author: [liushuo]
 * @CreateDate: [2015/3/3 11:10]
 * @Update:
 * @Version: [v1.0]
 */
public class TitleAnalyzer{

    public boolean analyze(List<String> logList,String tableName){
        LogUtil.debug("fock join analyze start");
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        Future<Boolean> result = forkJoinPool.submit(new FileAnalyzer(logList,0,tableName));
        boolean flag = false;
        try{
            flag = result.get();
            LogUtil.debug("fock join analyze end");
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.err(e);
        }
        return flag;
    }

}