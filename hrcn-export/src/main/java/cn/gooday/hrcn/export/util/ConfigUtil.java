package cn.gooday.hrcn.export.util;
import cn.gooday.hrcn.common.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * [读取配置文件工具类]
 *
 * @ProjectName: [hrcn-export]
 * @Author: [Jon.K]
 * @CreateDate: [2015/2/10]
 * @Update: [说明本次修改内容] BY[][2015/2/10]
 * @Version: [v1.0]
 */
public class ConfigUtil {
    private static final Logger logger = LoggerFactory.getLogger(ConfigUtil.class);
    private static Properties props=new Properties();
    public static void load(String propUrl) {
        logger.debug("配置文件路径：{}",propUrl);
        ClassLoader loader=ConfigUtil.class.getClassLoader();
        InputStream ips=null;
        try {
            ips=loader.getResourceAsStream(propUrl);
            BufferedReader bf = new BufferedReader(new InputStreamReader(ips,"GBK"));
            props.load(bf);
        } catch (IOException e) {
            logger.error("读取配置文件出错！原因："+e.getMessage(),e);
        }finally {
            try {
                if(ips!=null)
                    ips.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static String getProperty(String key){
        return props.getProperty(key);
    }
    public static java.util.Set<String> keySet(){
        return props.stringPropertyNames();
    }
    public static Map<String,String> getPropsMap(){
        Map<String,String> returnMap=new HashMap<>();
        Set<Map.Entry<Object, Object>> entries = props.entrySet();
        for (Map.Entry entry : entries) {
            returnMap.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return returnMap;
    }

    /**
     * 获取字符型属性
     */
    public String getString(String key) {
        return props.getProperty(key);
    }
    /**
     * 获取数值型属性
     */
    public int getInt(String key) {
        try {
            return Integer.valueOf(props.getProperty(key, "0"));
        } catch (RuntimeException e) {
            LogUtil.err("配置项{}非数字：", key, e);
        }
        return 0;
    }
    /**
     * 获取数值型属性
     */
    public long getLong(String key) {
        try {
            return Long.parseLong(props.getProperty(key, "0"));
        } catch (RuntimeException e) {
            LogUtil.err("配置项{}非数字：", key, e);
        }
        return 0;
    }
}
