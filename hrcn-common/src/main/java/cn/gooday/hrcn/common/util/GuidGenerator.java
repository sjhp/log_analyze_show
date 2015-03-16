/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.util;

import java.util.UUID;

/**
 * [生成系统唯一数]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/2/10 22:30]
 * @Update: [说明本次修改内容] BY[tophawk][2015/2/10]
 * @Version: [v1.0]
 */
public class GuidGenerator {
    public static synchronized String getGUID() {
        return UUID.randomUUID().toString().replace("-","");
    }
}
