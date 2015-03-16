package cn.gooday.hrcn.export.export.dao;

import cn.gooday.hrcn.common.bean.LogBean;
import cn.gooday.hrcn.export.common.mongodb.MongodbBaseDao;
import cn.gooday.hrcn.export.common.page.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * [hrcn.export.dao]
 *
 * @ProjectName: [hrcn]
 * @Author: [Jon.K]
 * @CreateDate: [2015/2/27 18:34]
 * @Update: [说明本次修改内容] BY[Jon][2015/2/27 18:34]
 * @Version: [v1.0]
 */
@Repository("logDao")
public class LogDao extends MongodbBaseDao<LogBean>{
    public Pagination<LogBean> getPageLog(int pageNo, int pageSize) {
        Query query = new Query();
        return this.getPage(pageNo, pageSize, query);
    }

    /**
     * 通过条件去查询
     * @return
     */
    public LogBean findOne(Map<String, String> params) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.where("id").is(params.get("id"));
        query.addCriteria(criteria);
        return super.findOne(query);
    }

    /**
     * 暂时通过ＩＤ去修改title
     */
    public void updateEntity(String id, Map<String, String> params) {
        super.updateFirst(Query.query(Criteria.where("id").is(id)), Update.update("content", params.get("content")));
    }

    @Autowired
    @Qualifier("mongoTemplate")
    @Override
    protected void setMongoTemplate(MongoTemplate mongoTemplate) {
        super.mongoTemplate = mongoTemplate;
    }

    @Override
    protected Class<LogBean> getEntityClass() {
        return LogBean.class;
    }
}
