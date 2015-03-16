package cn.gooday.hrcn.export.export.controller;

import cn.gooday.hrcn.common.bean.BaseBean;
import cn.gooday.hrcn.common.constant.Constants;
import cn.gooday.hrcn.common.util.LogUtil;
import cn.gooday.hrcn.common.util.StringUtil;
import cn.gooday.hrcn.export.common.page.Pagination;
import cn.gooday.hrcn.export.export.service.IExport;
import cn.gooday.hrcn.export.util.ConfigUtil;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Pattern;

/**
 * [hrcn.export.controller]
 *
 * @ProjectName: [hrcn]
 * @Author: [Jon.K]
 * @CreateDate: [2015/2/28 10:34]
 * @Update: [说明本次修改内容] BY[Jon][2015/2/28 10:34]
 * @Version: [v1.0]
 */
@Controller
@RequestMapping(value="/")
public class BaselogController {
    @Autowired
    public IExport baseLogExportImpl;
    @Autowired
    public IExport loginLogExportImpl;
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 首页日志查询页面
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value="log/toSearch.do")
    public ModelAndView logList(HttpServletRequest request,Model model) {
        return new ModelAndView("log_list").addObject("key",request.getParameter("key")).addObject("tablename",request.getParameter("tablename"));
    }

    /**
     * 异步分页查询
     * @param key
     * @param tablename
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value="log/search.do")
    public void datatablesPage(@RequestParam(value="iDisplayStart")int iDisplayStart,@RequestParam(value="iDisplayLength")int pageSize,@RequestParam(value="key")String key,@RequestParam(value="tablename")String tablename,HttpServletRequest request,HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("utf-8");
        PrintWriter printWriter=response.getWriter();
        /*int iDisplayStart=0;
        int pageSize=10;
        try {
            JSONArray jsonarray = JSONArray.parseArray(jsondata);
            for(Object object:jsonarray){
                JSONObject jsonObject=(JSONObject)object;
                if(jsonObject.get("name").equals("iDisplayStart")){
                    iDisplayStart = Integer.parseInt(jsonObject.get("value").toString());
                }
                if(jsonObject.get("name").equals("iDisplayLength")){
                    pageSize = Integer.parseInt(jsonObject.get("value").toString());
                }
            }
        }catch (Exception e){
            LogUtil.warn("illegal arguments :{}",e.getMessage());
            iDisplayStart=0;
            pageSize=10;
        }*/
        Criteria criteria =new Criteria();
        if(!StringUtil.isBlank(key)){
            //模糊匹配，忽略大小写
            Pattern pattern=Pattern.compile("^.*"+ HtmlUtils.htmlEscape(key) + ".*$", Pattern.CASE_INSENSITIVE);
            //或查询 匹配所有字段
            criteria.orOperator(Criteria.where("content").regex(pattern)
                    , Criteria.where("tomcatName").regex(pattern)
                    , Criteria.where("serverIp").regex(pattern),
                    Criteria.where("id").is(key));
        }
        //id降序排序
        Query query=Query.query(criteria).with(new Sort(new Sort.Order(Sort.Direction.DESC, "id")));
        Pagination<BaseBean> page = baseLogExportImpl.getPageLog(iDisplayStart / pageSize + 1, pageSize, tablename,query);
        printWriter.write(beanToJson(page));
        printWriter.flush();
        printWriter.close();
    }

    /**
     * 首页
     * @return
     */
    @RequestMapping(value="log/index.do")
    public ModelAndView  index(){
        return new ModelAndView("index").addObject("names",  getClientTableMap());
    }
    /**
     * 首页
     * @return
     */
    @RequestMapping(value="log/clientMonitor.do")
    public ModelAndView  clientMonitor(){
        List<List<String>> clientsStatus=new ArrayList<List<String>>();
        Map<String,String> tables=getClientTableMap();
        for(String client:tables.keySet()){
            String tempClient=client.replace(Constants.BASE_LOG_ALL,"");
            List<String> temp=new ArrayList<String>();
            DBObject dbObject =new BasicDBObject();
            dbObject.put("client_id",tempClient);
            dbObject.put("send_date","2015-03-13");//new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            dbObject=mongoTemplate.getCollection(Constants.CLIENT_RUNTIME_INFO).findOne(dbObject);
            if(dbObject==null)continue;
            System.err.println(dbObject);
            temp.add(tempClient);
            temp.add(tables.get(client));
            temp.add(dbObject.get("client_start_time").toString());
            temp.add(dbObject.get("client_last_time").toString());
            temp.add(dbObject.get("server_last_time").toString());
            temp.add(dbObject.get("exceptions").toString());
            clientsStatus.add(temp);
        }
        return new ModelAndView("clientMonitor").addObject("clients", tables).addObject("clientsStatus",clientsStatus);
    }
    private Map<String,String> getClientTableMap(){
        //获取db中所有存储原日志的表名
        Set<String> names= mongoTemplate.getCollectionNames();
        //获取配置文件中的服务
        ConfigUtil.load("tablenames.properties");
        Set<String> set=ConfigUtil.keySet();
        Map<String,String> tables=new HashMap<String,String>();
        for(String name:names){
            //判断是否存储原始日志内容的表
            if(name.indexOf( Constants.BASE_LOG_ALL)>-1){
                String cName=ConfigUtil.getProperty(name.replace(Constants.BASE_LOG_ALL,""));
                tables.put(name,(cName==null?name.replace(Constants.BASE_LOG_ALL,""):cName));
            }
        }
        return tables;
    }
    /**
     * 组装datatables分页参数
     * @param page
     * @return
     */
    private String beanToJson( Pagination<BaseBean> page){
        Map map=new HashMap<>();
        map.put("iDisplayStart",page.getFirstResult());
        map.put("iTotalRecords",page.getTotalCount());
        map.put("iTotalDisplayRecords",page.getTotalCount());
        //List<List> data=new ArrayList<List>();
        for(BaseBean logBean:page.getDatas()){
           /* List a=new ArrayList();
            a.add(logBean.getId());
            a.add(logBean.getTomcatName());
            a.add(logBean.getServerIp());
            //转义特殊字符
            a.add(HtmlUtils.htmlEscape(logBean.getContent()));
            data.add(a);*/
            logBean.setContent(HtmlUtils.htmlEscape(logBean.getContent()));
        }
        map.put("aaData", page.getDatas());
        String ss=JSONObject.toJSON(map).toString();
        LogUtil.debug("jsonStr={}",ss);
        return ss;
    }
}
