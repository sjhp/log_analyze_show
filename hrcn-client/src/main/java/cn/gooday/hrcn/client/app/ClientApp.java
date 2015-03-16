/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.client.app;

import cn.gooday.hrcn.client.bootstrap.ClientBootStrap;
import cn.gooday.hrcn.common.util.LogUtil;

/**
 * [简短描述该类的功能]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/2/14 21:42]
 * @Update: [说明本次修改内容] BY[tophawk][2015/2/14]
 * @Version: [v1.0]
 */
public class ClientApp {
    /**
     * 客户端入口
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            ClientBootStrap bootStrap = new ClientBootStrap();
            //系统初始化并启动
            if (!bootStrap.init()) {
                LogUtil.err("Client init failure,system halted!");
                System.exit(-1);
            }
            bootStrap.start();
            LogUtil.info("Client started!");
        } catch (Exception ex) {
            ex.printStackTrace();
            LogUtil.err("Client init failure,system halted!");
        }
    }
}
