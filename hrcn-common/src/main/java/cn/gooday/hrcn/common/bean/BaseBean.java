package cn.gooday.hrcn.common.bean;

import java.io.Serializable;

/**
 * [原生日志bean]
 *
 * @ProjectName: [hrcn]
 * @Author: [Jon.K]
 * @CreateDate: [2015/3/2 18:45]
 * @Update: [说明本次修改内容] BY[Jon][2015/3/2 18:45]
 * @Version: [v1.0]
 */
public class BaseBean implements Serializable{
    /**
     * ID
     */
    public String id;
    /**
     * 服务器信息
     */
    public String serverIp;
    /**
     * tomcat 信息
     */
    public String tomcatName;
    /**
     * 日志内容
     */
    public String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getTomcatName() {
        return tomcatName;
    }

    public void setTomcatName(String tomcatName) {
        this.tomcatName = tomcatName;
    }

    @Override
    public String toString() {
        return "id:"+id+" ;content:"+content+"; serverIp:"+serverIp+"; tomcatName:"+tomcatName;
    }
}
