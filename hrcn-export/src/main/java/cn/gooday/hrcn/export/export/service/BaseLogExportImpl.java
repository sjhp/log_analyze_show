package cn.gooday.hrcn.export.export.service;

import cn.gooday.hrcn.common.bean.BaseBean;
import cn.gooday.hrcn.common.util.LogUtil;
import cn.gooday.hrcn.export.common.page.Pagination;
import cn.gooday.hrcn.export.util.Tools;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * [hrcn.export.service]
 *
 * @ProjectName: [hrcn]
 * @Author: [Jon.K]
 * @CreateDate: [2015/3/6 10:55]
 * @Update: [说明本次修改内容] BY[Jon][2015/3/6 10:55]
 * @Version: [v1.0]
 */
@Service("baseLogExportImpl")
public class BaseLogExportImpl implements IExport<BaseBean> {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public void save(BaseBean log, String collectionName){
        mongoTemplate.save(log,collectionName);
    }

    @Override
    public BaseBean findById(String id, String collectionName) {
        return mongoTemplate.findById(id,BaseBean.class,collectionName);
    }
    @Override
    public List<BaseBean> findALL(String collectionName) {
        return mongoTemplate.findAll(BaseBean.class,collectionName);
    }

    @Override
    public BaseBean findOne(Map<String, String> params) {
        //mongoTemplate.fin
        return null;
    }
    @Override
    public long count(String collectionName, Map<String,Object> filter) {
        Query query = Tools.createQuery(filter,null);
        return mongoTemplate.count(query,BaseBean.class,collectionName);
    }
    @Override
    public Pagination<BaseBean> getPageLog(int pageNo, int pageSize, String collectionName,Query query) {
        long totalCount = mongoTemplate.count(query,BaseBean.class,collectionName);

        Pagination<BaseBean> page = new Pagination<BaseBean>(pageNo, pageSize, totalCount);
        // skip相当于从那条记录开始
        query.skip(page.getFirstResult());
        // 从skip开始,取多少条记录
        query.limit(pageSize);
        page.setDatas(mongoTemplate.find(query, BaseBean.class, collectionName));
        return page;
    }
    @Override
    public Pagination<BaseBean> getPageLog(int pageNo, int pageSize, String collectionName, Map<String,Object> filter, Map<String,String> sorts) {
        Query query =Tools.createQuery(filter,sorts);
        long totalCount = mongoTemplate.count(query,BaseBean.class,collectionName);

        Pagination<BaseBean> page = new Pagination<BaseBean>(pageNo, pageSize, totalCount);
        // skip相当于从那条记录开始
        query.skip(page.getFirstResult());
        // 从skip开始,取多少条记录
        query.limit(pageSize);
        page.setDatas(mongoTemplate.find(query, BaseBean.class, collectionName));
        return page;
    }

    @Override
    public void update(String id, Map params, Class<BaseBean> entityClass, String collectionName) {
        Update update=new Update();
        Set<Map.Entry> entrySet=params.entrySet();
        for(Map.Entry entry:entrySet){
            update.update(entry.getKey().toString(),entry.getValue());
        }
        Criteria criteria = Criteria.where("id").gt(id);
        WriteResult result=mongoTemplate.updateFirst(new Query(criteria), update, entityClass, collectionName);
        //WriteResult result=mongoTemplate.findAndModify(new Query(criteria),update,entityClass,collectionName);
        LogUtil.debug("更新记录数：{}",result.getN());
    }
}
