/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.server.app;

import cn.gooday.hrcn.server.bootstrap.ServerBootStrap;
import cn.gooday.hrcn.common.util.LogUtil;

/**
 * [服务端功能]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/2/14 21:42]
 * @Update: [说明本次修改内容] BY[tophawk][2015/2/14]
 * @Version: [v1.0]
 */
public class ServerApp {
    /**
     * 服务端入口
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            ServerBootStrap bootStrap = new ServerBootStrap();
            //系统初始化并启动
            if (!bootStrap.init()) {
                LogUtil.err("server init failure,system halted!");
                System.exit(-1);
            }
            bootStrap.start();
            LogUtil.info("server started!");
        } catch (Exception ex) {
            ex.printStackTrace();
            LogUtil.err("server init failure,system halted!");
        }
    }
}
