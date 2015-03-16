/*
 * 巨商汇平台 版权所有 Copyright@2014
 */
package cn.gooday.hrcn.common.util;

import cn.gooday.hrcn.common.config.ConfigService;
import cn.gooday.hrcn.common.event.Event;
import cn.gooday.hrcn.common.event.events.CollectFinishedEvent;
import cn.gooday.hrcn.common.event.events.DataCollectedEvent;
import cn.gooday.hrcn.common.event.events.DataReceivedEvent;
import cn.gooday.hrcn.common.exception.RemotingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * [记录客户端运行状态]
 *
 * @ProjectName: [hrcn]
 * @Author: [tophawk]
 * @CreateDate: [2015/3/12 14:35]
 * @Update: [说明本次修改内容] BY[tophawk][2015/3/12]
 * @Version: [v1.0]
 */
public class StatUtil {
    private static final Map<String, Object> statMap = new HashMap<>();

    /**
     * 初始化
     */
    public static void init() {
        ConfigService cs = RtUtil.getConfigService();
        if (cs == null) throw new RuntimeException("config load error！");

        String ip = cs.getString("local.ip.address");
        //String tomcat = cs.getString("collect.fail.path");
        statMap.put("client_id", StringUtil.join(ip));
        statMap.put("client_start_time", DateUtil.getTodayYYYYMMDD_HHMMSS());
    }

    /**
     * 统计
     *
     * @param e
     * @param ex
     */
    public static void stat(Event e, Exception... ex) {
        statMap.put("client_last_time", DateUtil.getTodayYYYYMMDD_HHMMSS());
        List<Map<String, Object>> lstEx = (List<Map<String, Object>>) statMap.getOrDefault("exceptions", new ArrayList<>());
        boolean found = false;
        List lstExRep = new ArrayList<>();
        for (Map<String, Object> m : lstEx) {
            if (m.containsValue(e.getEventType())) {
                found = true;
                int sum = (int) m.getOrDefault("sum", 0);
                sum += 1;
                m.put("sum", sum);
                m.put("last_time", DateUtil.getTodayYYYYMMDD_HHMMSS());
                if (ex.length > 0)
                    m.put("last_cause", ex[0].getMessage());
                m.put("last_data_no", e.getEventNo());
            }
            lstExRep.add(m);
        }
        if (!found) {
            Map<String, Object> map = new HashMap<>();
            map.put("event_type", e.getEventType());
            map.put("sum", 0);
            map.put("last_time", DateUtil.getTodayYYYYMMDD_HHMMSS());
            if (ex.length > 0)
                map.put("last_cause", ex[0].getMessage());
            map.put("last_data_no", e.getEventNo());
            lstExRep.add(map);
        }
        lstEx.clear();
        statMap.put("exceptions", lstExRep);
    }

    public static String toJson() {
        return JsonUtil.toJSON(statMap);
    }

    public static void main(String args[]) {
        StatUtil.init();
        DataCollectedEvent event1 = new DataCollectedEvent();
        event1.setEventNo("12");
        CollectFinishedEvent event2 = new CollectFinishedEvent();
        event2.setEventNo("123");
        DataReceivedEvent event3 = new DataReceivedEvent();
        DataReceivedEvent event4 = new DataReceivedEvent();
        DataReceivedEvent event5 = new DataReceivedEvent();
        DataReceivedEvent event6 = new DataReceivedEvent();
        //StatUtil.stat(event1);
        //StatUtil.stat(event2);
        StatUtil.stat(event3, new RemotingException("error occored"));
        StatUtil.stat(event4);
        StatUtil.stat(event5);
        StatUtil.stat(event6);

        System.out.println(StatUtil.toJson());
    }
}
