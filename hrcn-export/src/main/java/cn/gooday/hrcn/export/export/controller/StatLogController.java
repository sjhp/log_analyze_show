package cn.gooday.hrcn.export.export.controller;

import cn.gooday.hrcn.common.constant.Constants;
import cn.gooday.hrcn.common.util.LogUtil;
import cn.gooday.hrcn.common.util.StringUtil;
import cn.gooday.hrcn.export.export.service.IExport;
import cn.gooday.hrcn.export.util.ConfigUtil;
import cn.gooday.hrcn.export.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * [日志展示系统统计Controller]
 *
 * @ProjectName: [hrcn]
 * @Author: [Jon.K]
 * @CreateDate: [2015/3/9 14:15]
 * @Update: [说明本次修改内容] BY[Jon][2015/3/9 14:15]
 * @Version: [v1.0]
 */
@Controller
@RequestMapping(value="/stat")
public class StatLogController {
    @Autowired
    public IExport loginLogExportImpl;
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 今日各模块日志统计
     * @param request
     * @param view
     * @return
     */
    @RequestMapping(value="/currentStatis.do")
    public ModelAndView currentStatis(HttpServletRequest request,ModelAndView view) {
        List dateList=new ArrayList<>();
        String nowDate=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        //获取配置的动作代码
        ConfigUtil.load("config.properties");
        String actionCodes= ConfigUtil.getProperty("ActionCodes");
        List<String> actionCodeList=new ArrayList<String>();
        if(!StringUtil.isBlank(actionCodes)){
            actionCodeList=Arrays.asList(actionCodes.split(","));
        }else {
            throw new IllegalArgumentException("please check arguments!");
        }
        Map dataMap=getActionCount(nowDate, actionCodeList);
        view.setViewName("currentStatis");
        return view.addObject("actionCodeList",getCodeStr(actionCodeList)).addObject("countArray",getCountArray(dataMap,nowDate,actionCodeList)).addObject("now",nowDate);
    }

    /**
     * 图表统计页面
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value="/showStat.do")
    public ModelAndView showStat(HttpServletRequest request,Model model) {
        return stat(request,new ModelAndView("showStat"));
    }

    /**
     * 时间段各模块日志统计统计
     * @param request
     * @return
     */
    @RequestMapping(value="/statis.do")
    public ModelAndView statis(HttpServletRequest request) {
        return stat(request,new ModelAndView("statis"));
    }
    private String getCountArray(Map<String,Map> dataMap,String date,List<String> actionCodeList){
        List data=new ArrayList<>();
        for(String code:actionCodeList){
            data.add(dataMap.get(date).get(code)==null?0:dataMap.get(date).get(code));
        }
        LogUtil.debug("CountArray={}",Arrays.toString(data.toArray()));
        return Arrays.toString(data.toArray());
    }

    /**
     * 字符串处理
     * @param actionCodeList
     * @return
     */
    private String getCodeStr(List<String> actionCodeList){
        List data=new ArrayList<>();
        for(String code:actionCodeList){
            data.add("'"+code+"'");
        }
        LogUtil.debug("codestr={}",Arrays.toString(data.toArray()));
        return Arrays.toString(data.toArray());
    }

    private ModelAndView stat(HttpServletRequest request,ModelAndView view) {
        String startTime=request.getParameter("startTime");
        String endTime=request.getParameter("endTime");
        //开始时间
        Date start;
         //结束时间
        Date end;
        //默认过去一周的时间
        if(StringUtil.isBlank(startTime)||StringUtil.isBlank(endTime)){
            start=DateUtil.addDayOfMonth(new Date(),-7,true);
            end=Calendar.getInstance().getTime();
        }else{
             start=DateUtil.reverse2Date(startTime);
             end=DateUtil.reverse2Date(endTime);
        }
        if(start.after(end)){
            throw new IllegalArgumentException("please check arguments!");
        }
        //获取配置的actioncode
        ConfigUtil.load("config.properties");
        String actionCodes= ConfigUtil.getProperty("ActionCodes");
        List<String> actionCodeList=new ArrayList<>();
        if(!StringUtil.isBlank(actionCodes)){
            actionCodeList=Arrays.asList(actionCodes.split(","));
        }else {
            throw new IllegalArgumentException("please check arguments!");
        }
        //循环查询每天的所有动作的统计计数
        DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        Calendar startC=Calendar.getInstance();
        startC.setTime(start);
        Calendar endC=Calendar.getInstance();
        endC.setTime(end);
        Map dataMap=new HashMap<>();
        List dateList=new ArrayList<>();
        while(startC.compareTo(endC) <= 0) {
            dateList.add(df.format(startC.getTime()));
            dataMap.putAll(getActionCount(df.format(startC.getTime()), actionCodeList));
            //循环，每次天数加1
            startC.set(Calendar.DATE, startC.get(Calendar.DATE) + 1);
        }
        //返回界面，
        LogUtil.debug("dateList={}",getCodeStr(dateList));
        view.addObject("dateList",getCodeStr(dateList));
        for(String code:actionCodeList){
            LogUtil.debug("ActionCode={}; count={}",code,toView(dataMap,code,dateList));
            view.addObject(code, toView(dataMap, code, dateList));
        }
        return view;
    }
    private String toView(Map<String,Map> dataMap,String code,List<String> dateList){
        List data=new ArrayList<>();
        for(String date:dateList){
            data.add(dataMap.get(date).get(code));
        }
        return Arrays.toString(data.toArray());
    }

    /**
     * 获取某一天的所有动作的统计计数
     * @param date
     * @param actionCodeList
     * @return
     */
    private Map getActionCount(String date,List<String> actionCodeList){
        Map<String,Long> returnMap=new HashMap<>();
        Map filter=new HashMap<>();
        filter.put("time",date);
        for(String name: mongoTemplate.getCollectionNames()){
            //判断是否是存储解析后日志的表名
            if(name.indexOf( Constants.BASE_LOG_ANALYZE)>-1){
                for(String code:actionCodeList){
                    filter.put("ActionCode",code);
                    returnMap.put(code,(returnMap.get(code)==null?0:returnMap.get(code))+loginLogExportImpl.count(name,filter));
                }
            }
        }
        Map temp=new HashMap();
        temp.put(date,returnMap);
        return temp;
    }
}
