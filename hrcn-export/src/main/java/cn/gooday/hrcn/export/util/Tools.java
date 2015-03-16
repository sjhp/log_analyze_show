package cn.gooday.hrcn.export.util;

import cn.gooday.hrcn.common.util.StringUtil;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * [cn.gooday.hrcn.export.util]
 *
 * @ProjectName: [hrcn]
 * @Author: [Jon.K]
 * @CreateDate: [2015/3/13 15:49]
 * @Update: [说明本次修改内容] BY[Jon][2015/3/13 15:49]
 * @Version: [v1.0]
 */
public class Tools {
    /**
     *
     * @param filter
     * @param sorts
     * @return
     */
    public static Query createQuery(Map<String,Object> filter,Map<String,String> sorts) {
        Query query = new Query();
        Set<Map.Entry<String,Object>> set= filter.entrySet();
        Criteria criteria = new Criteria();
        Criteria[] criteriaArray = new Criteria[filter.size()];
        int i=0;
        for(Map.Entry entry:set){
            if(entry.getValue() instanceof String){
                //模糊匹配，忽略大小写
                Pattern pattern=Pattern.compile("^.*"+entry.getValue()+".*$",Pattern.CASE_INSENSITIVE);
                criteriaArray[i]=Criteria.where(entry.getKey().toString()).regex(pattern);
            }else{
                criteriaArray[i]= Criteria.where(entry.getKey().toString()).is(entry.getValue());
            }
            i++;
        }
        query.addCriteria(criteria.andOperator(criteriaArray));
        //处理排序
        if(sorts!=null && sorts.size()>0){
            for(Map.Entry sort:sorts.entrySet()){
                if(!StringUtil.isBlank(sort.getValue().toString())){
                    if(Sort.Direction.ASC.toString().equalsIgnoreCase(sort.getValue().toString())){
                        query.with(new Sort(new Sort.Order(Sort.Direction.ASC, sort.getKey().toString())));
                    }else if(Sort.Direction.DESC.toString().equalsIgnoreCase(sort.getKey().toString())){
                        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, sort.getKey().toString())));
                    }
                }
            }
        }else{
            query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "id")));
        }
        return query;
    }
}
