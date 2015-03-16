package cn.gooday.hrcn.export.export.service;

import cn.gooday.hrcn.export.common.page.Pagination;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Map;

/**
 * [com.gooday.hrcn.export]
 *
 * @ProjectName: [hrcn]
 * @Author: [Jon.K]
 * @CreateDate: [2015/2/25]
 * @Update: [说明本次修改内容] BY[Jon][2015/2/25]
 * @Version: [v1.0]
 */
public interface IExport<T> {
    /**
     * 保存
     * @param log
     * @param collectionName
     */
    void save(T log, String collectionName);

    /**
     * 根据id查询
     * @param id
     * @param collectionName
     * @return
     */
    T findById(String id, String collectionName);

    /**
     * 便利表中所有数据
     * @param collectionName
     * @return
     */
    List<T> findALL(String collectionName);
    T findOne(Map<String, String> params);

    /**
     * 多条件查询（and关系）表中数据
     * @param collectionName
     * @param filter
     * @return
     */
    long count( String collectionName, Map<String,Object> filter);

    /**
     * 自定义query查询分页数据
     * @param pageNo
     * @param pageSize
     * @param collectionName
     * @param query
     * @return
     */
    Pagination<T> getPageLog(int pageNo, int pageSize, String collectionName, Query query);

    /**
     * 多条件（条件之间为并且关系）查询分页数据
     * @param pageNo
     * @param pageSize
     * @param collectionName
     * @param filter
     * @param sort
     * @return
     */
    Pagination<T> getPageLog(int pageNo, int pageSize, String collectionName, Map<String,Object> filter,Map<String,String> sort);

    /**
     * 更新
     * @param id
     * @param params
     * @param entityClass
     * @param collectionName
     */
    void update(String id, Map params, Class<T> entityClass, String collectionName);
}
