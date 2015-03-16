/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.exception;

/**
 * [收集异常，通常是收集工具无法处理的异常]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/2/10 22:06]
 * @Update: [说明本次修改内容] BY[tophawk][2015/2/10]
 * @Version: [v1.0]
 */
public class CollectException extends BaseRuntimeException {
    public CollectException(String msg){
        super(msg);
        //TO DO: 可以选择发往服务器报警
    }
}
