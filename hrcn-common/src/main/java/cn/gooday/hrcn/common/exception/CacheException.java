/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.exception;

/**
 * [缓存异常，通常是缓存工具无法处理的异常，比如磁盘满等]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/2/10 22:06]
 * @Update: [说明本次修改内容] BY[tophawk][2015/2/10]
 * @Version: [v1.0]
 */
public class CacheException extends BaseRuntimeException {
    public CacheException(String msg){
        super(msg);
        //TO DO: 可以选择发往服务器报警
    }
}
