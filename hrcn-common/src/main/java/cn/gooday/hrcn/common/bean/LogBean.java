package cn.gooday.hrcn.common.bean;

import java.io.Serializable;

/**
 * [解析后日志bean]
 *
 * @ProjectName: [hrcn]
 * @Author: [Jon.K]
 * @CreateDate: [2015/2/25 11:47]
 * @Update: [说明本次修改内容] BY[Jon.K][2015/2/25 11:47]
 * @Version: [v1.0]
 */
public class LogBean implements Serializable {
    private String id;
    /**
     * 动作代码
     */
    private String actionCode;
    /**
     * 日志级别
     */
    private String level;
    /**
     * 主体
     */
    private String main;
    /**
     * 客体
     */
    private String subject;
    /**
     * 动作类型
     */
    private String actionType;
    /**
     * 日志打印日期：yyyy-MM-ddhh:mm:ss
     */
    private String time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSubject(){ return subject; }

    public void setSubject(String subject){
        this.subject = subject;
    }

    public String getActionType(){
        return actionType;
    }

    public void setActionType(String actionType){
        this.actionType = actionType;
    }

    @Override
    public String toString() {
        return "id:"+id+"; actionCode:"+actionCode+"; level:"+level+"; main:"+main+"; subject:"+subject+"; actionType:"+actionType+"; time:"+time;
    }
}
