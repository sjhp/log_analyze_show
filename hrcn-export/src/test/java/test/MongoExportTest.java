package test;

import cn.gooday.hrcn.common.bean.BaseBean;
import cn.gooday.hrcn.common.bean.LogBean;
import cn.gooday.hrcn.common.constant.Constants;
import cn.gooday.hrcn.common.util.MongoDBManager;
import cn.gooday.hrcn.export.common.page.Pagination;
import cn.gooday.hrcn.export.export.service.IExport;
import cn.gooday.hrcn.export.util.ConfigUtil;
import cn.gooday.hrcn.export.util.Tools;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.util.HtmlUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * [PACKAGE_NAME]
 *
 * @ProjectName: [hrcn]
 * @Author: [Jon.K]
 * @CreateDate: [2015/2/26 16:27]
 * @Update: [说明本次修改内容] BY[Jon][2015/2/26 16:27]
 * @Version: [v1.0]
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext.xml")
public class MongoExportTest {
    @Autowired
    public IExport baseLogExportImpl;
    @Autowired
    public IExport loginLogExportImpl;
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 保存
     * @throws IOException
     */
    @Test
    public void exportToDB()  throws IOException {
        File f=new File("D:\\log\\daily.log");
        RandomAccessFile raf = new RandomAccessFile(f, "r");
        String line;
        ConfigUtil.load("tablenames.properties");
        Set set=ConfigUtil.keySet();
        while ((line = raf.readLine()) != null) {
            BaseBean logBean=new BaseBean();
            logBean.setContent(line);
            for(Object o:set){
                baseLogExportImpl.save(logBean,o.toString());
            }
        }
        raf.close();
        //DynamicProxy proxy=new DynamicProxy(new MongoExport());
        // IExport export=proxy.getProxy();
        // CGLibProxy.getInstance().getProxy(MongoExport.class).save(logBean);
    }
    @Test
    public void testFind() {
        ConfigUtil.load("tablenames.properties");
        Set set=ConfigUtil.keySet();
        List<BaseBean> base=baseLogExportImpl.findALL("192.168.0.1buyerall");
        for(BaseBean bean:base){
            System.err.println(bean);
        }
        List<LogBean> logs=loginLogExportImpl.findALL("192.168.0.1buyeranalyze");

        for(LogBean bean:logs){
            System.err.println(bean);
        }
        String actionCodes="register,seller,buyer,ompinterface,product,order,pay,payinterface,paycallback,message,recharge,exception,login,logout,view";
        List temp=Arrays.asList(actionCodes.split(","));
        System.err.println(Arrays.toString(temp.toArray()));
    }

    /**
     * 输出所有表名
     */
    @Test
    public void testfindAllTables() {
        for(String name: mongoTemplate.getCollectionNames()){
            System.err.println(name);
        }
    }

    /**
     * 查询db下的所有内容
     */
    @Test
    public void testFindALL() {
        for(String name:mongoTemplate.getCollectionNames()){
            System.err.println(name);
            List<BaseBean> list=mongoTemplate.findAll(BaseBean.class,name);
           list.forEach(System.err::println);
            List<LogBean> list2=mongoTemplate.findAll(LogBean.class,name);
            list2.forEach(System.err::println);
        }
    }

    /**
     * 保存
     */
    @Test
    public void testSave() {
        LogBean log=new LogBean();
        log.setActionCode("login");
        log.setLevel("debug");
        log.setMain("sure2048,13422222222");
        log.setSubject("XXXX");
        log.setTime("2015-03-0918:12:59");
        //mongoTemplate.save(log,"192.168.0.1buyeranalyze");
        System.err.println(mongoTemplate.findAll(BaseBean.class).size());
    }

    /**
     * 测试更新
     */
    @Test
    public void testUpdate(){
        LogBean log=new LogBean();
        log.setActionCode("login");
        log.setLevel("debug");
        log.setMain("sure2048,13422222222");
        log.setSubject("XXXX");
        log.setTime("2015-03-0918:12:59");
        ///loginLogExportImpl.update(  );
    }
    /**
     * 删除表
     */
    @Test
    public void deleteTable() {
        //mongoTemplate.dropCollection(BaseBean.class);
        mongoTemplate.dropCollection("58.135.80.27-D:\\Program Files\\SoftEther VPN Client\\backup.vpn_client.config");
    }
    /**
     * 删除数据库
     */
    @Test
    public void dropDatabase() {
        mongoTemplate.getDb().getMongo().dropDatabase("db");
        //mongoTemplate.dropCollection(BaseBean.class);
        /*for(String name:mongoTemplate.getDb().getMongo().getDatabaseNames()){
            System.err.println(name);
        }*/
    }
    /**
     * 删除数据
     */
    @Test
    public void dropData() {
        Map filer2=new HashMap<>();
        filer2.put("content",null);
        filer2.put("serverIp",null);
        filer2.put("tomcatName",null);
       for(String name: mongoTemplate.getCollectionNames()){
            System.err.println(name);
            List<BaseBean> list=mongoTemplate.find(Tools.createQuery(filer2, null),BaseBean.class,name);
           for(BaseBean bean:list){
               System.err.println(bean);
               mongoTemplate.remove(bean);
           }
        }
        /*Map filer=new HashMap<>();
        filer.put("actionCode",null);
        filer.put("main",null);
        filer.put("subject",null);
        for(String name: mongoTemplate.getCollectionNames()){
            System.err.println(name);
            List<LogBean> list=mongoTemplate.findAllAndRemove(Tools.createQuery(filer, null),LogBean.class,name);
            list.forEach(System.err::println);
        }*/
    }
    /**
     * 测试分页
     */
    @Test
    public void testPage(){
        Map filter=new HashMap<>();
        filter.put("actionCode","login");
        //filter.put("main","18611559764");
        Map sort=new HashMap<>();
        sort.put("ID", Sort.Direction.DESC);
        sort.put("time", Sort.Direction.DESC);
        Pagination<LogBean> page = loginLogExportImpl.getPageLog(1, 100,"192.168.0.242selleranalyze",filter,sort);
        for(LogBean bean:page.getDatas()){
            System.err.println(bean);
        }
   /*     Map filter=new HashMap<>();
        Map sort=new HashMap<>();
        Pagination<BaseBean> page = baseLogExportImpl.getPageLog(1, 1000,"192.168.0.242sellerall",filter,new HashMap<>());
        for(BaseBean bean:page.getDatas()){
           // System.err.println(bean);
        }*/
        Criteria criteria = Criteria.where("_id").is("55017e0cb7602218b405038e");
     /*   MongoDBManager mongoDBManager=new MongoDBManager();
        DBCursor lsit=mongoDBManager.findAll2("192.168.0.242sellerall");
        while (lsit.hasNext()){
            System.err.println(lsit.next());
        }
        for(DBObject o:lsit){
            System.err.println(o);
        }*/
        /*for(BaseBean bean:mongoTemplate.find(Query.query(criteria),BaseBean.class,"192.168.0.242sellerall")){
            System.err.println(bean);
        }*/
    }

    /**
     * 测试
     */
    @Test
    public void testIndex(){
        Set<String> names= mongoTemplate.getCollectionNames();
        ConfigUtil.load("tablenames.properties");
        Set set=ConfigUtil.keySet();
        Map<String,String> tables=new HashMap<String,String>();
        long loginCount=0;
        long smsCount=0;
        long registerCount=0;
        Map filter=new HashMap<>();
        DateFormat df=new SimpleDateFormat("yyyy-MM-dd");

        filter.put("time","2015-03-09");

        for(String name:names){
            for(Object s:set){
                if(name.equalsIgnoreCase((String) s)){
                    tables.put(name,ConfigUtil.getProperty((String) s));
                    name="192.168.0.1buyeranalyze";
                    filter.put("ActionCode","login");
                    loginCount=loginLogExportImpl.count(name,filter);
                    System.err.println("loginCount="+loginCount);
                    filter.put("ActionCode","message");
                    smsCount=loginLogExportImpl.count(name,filter);
                    System.err.println("smsCount="+smsCount);
                    filter.put("ActionCode","register");
                    registerCount=loginLogExportImpl.count(name,filter);
                    System.err.println("registerCount="+registerCount);
                }
            }
        }
    }
    @Test
    public void testMongoBson(){
       // DBObject dbObject = (DBObject) JSON.parse("{'client_id':'192.168.63.1tomcat-selle6','client_start_time': '2015-10-13 10:10:00','client_last_time': '2015-10-13 10:10:00','server_last_time': '2015-10-12 10:10:00','exceptions': [    {       'event_type': 'DATA_COLLECTED_EVENT',        'sum': '100',       'last_time': '2015-10-12 10:10:00','last_data_no': 'event no',       'last_cause': 'ex.getMessage()'      },       {        'event_type': 'DATA_COLLECTED_EVENT',        'sum': '100',         'last_time': '2015-10-12 10:10:00',          'last_data_no': 'event no',        'last_cause': 'ex.getMessage()'     },   {       'event_type': 'DATA_COLLECTED_EVENT',      'sum': '100', 'last_time': '2015-10-12 10:10:00',    'last_data_no': 'event no',     'last_cause': 'ex.getMessage()'    }  ]}");
        //mongoTemplate.insert(dbObject, Constants.CLIENT_RUNTIME_INFO);
        DBObject dbObject =new BasicDBObject();
        //dbObject.put("client_id","192.168.0.122:tomcat0");
        //dbObject.put("send_date",new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        DBCursor cursor = mongoTemplate.getCollection(Constants.CLIENT_RUNTIME_INFO).find();
        while (cursor.hasNext()){
            System.err.println(cursor.next());
        }
    }
    @Test
    public void initBson(){
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
                tables.put(name,(cName==null?name:cName));
                String bson="{'client_id':'"+name.replace(Constants.BASE_LOG_ALL,"")+"','client_start_time': '2015-10-13 10:10:00','client_last_time': '2015-10-13 10:10:00','server_last_time': '2015-10-12 10:10:00','exceptions': [    {       'event_type': 'DATA_COLLECTED_EVENT',        'sum': '100',       'last_time': '2015-10-12 10:10:00','last_data_no': 'event no',       'last_cause': 'ex.getMessage()'      },       {        'event_type': 'DATA_COLLECTED_EVENT',        'sum': '100',         'last_time': '2015-10-12 10:10:00',          'last_data_no': 'event no',        'last_cause': 'ex.getMessage()'     },   {       'event_type': 'DATA_COLLECTED_EVENT',      'sum': '100', 'last_time': '2015-10-12 10:10:00',    'last_data_no': 'event no',     'last_cause': 'ex.getMessage()'    }  ]}";
                MongoDBManager.saveBsonStr(bson,Constants.CLIENT_RUNTIME_INFO);
            }
        }
    }
    @Test
    public void testMongoTim(){
        String specialStr = "<div id=\"testDiv\">test1;test2</div>";
        //①转换为HTML转义字符表示
        String str1 = HtmlUtils.htmlEscape(specialStr);
        System.out.println(str1);
        // ②转换为数据转义表示
        String str2 = HtmlUtils.htmlEscapeDecimal(specialStr);
        System.out.println(str2);
        //③转换为十六进制数据转义表示
        String str3 = HtmlUtils.htmlEscapeHex(specialStr);
        System.out.println(str3);
        //④下面对转义后字符串进行反向操作
        System.out.println(HtmlUtils.htmlUnescape(str1));
        System.out.println(HtmlUtils.htmlUnescape(str2));
        System.out.println(HtmlUtils.htmlUnescape(str3));
    }
    @Test
    public void testPattan(){
        String key="sdfsdf[]";
        System.err.println(test(key));
    }
    @Test
    public void saveBSONstr(){
       // mongoTemplate.dropCollection("client_info");
       // MongoDBManager.saveBsonStr("{'client_id':'192.168.0.123:tomcat1','client_start_time': '2015-1010:10:00','client_last_time': '2015-10-12 10:10:00','server_last_time': '2015-10-12 10:10:00','exceptions': [    {       'event_type': 'DATA_COLLECTED_EVENT',        'sum': '100',       'last_time': '2015-10-12 10:10:00','last_data_no': 'event no',       'last_cause': 'ex.getMessage()'      },       {        'event_type': 'DATA_COLLECTED_EVENT',        'sum': '100',         'last_time': '2015-10-12 10:10:00',          'last_data_no': 'event no',        'last_cause': 'ex.getMessage()'     },   {       'event_type': 'DATA_COLLECTED_EVENT',      'sum': '100', 'last_time': '2015-10-12 10:10:00',    'last_data_no': 'event no',     'last_cause': 'ex.getMessage()'    }  ]}", "client_info");
        DBCursor cursorDoc = MongoDBManager.getCollection("client_info").find();
        while (cursorDoc.hasNext()) {
            System.out.println(cursorDoc.next());
        }
    }
    private String test(String str){
        String re="";
        String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p   =   Pattern.compile(regEx);
        Matcher m   =   p.matcher(str);
        while (m.find()) {
            String temp = m.group(0);
            str = str.replace(temp, "\\" + temp);
        }
        return str;
    }
}
