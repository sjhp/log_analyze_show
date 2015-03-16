package cn.gooday.hrcn.common.util;

import cn.gooday.hrcn.common.config.ConfigService;
import com.mongodb.*;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;
import com.mongodb.util.JSON;
import org.bson.types.ObjectId;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * [mongodb工具类]
 *
 * @ProjectName: [hrcn]
 * @Author: [Jon.K]
 * @CreateDate: [2015/2/10]
 * @Update: [说明本次修改内容] BY[][2015/2/10]
 * @Version: [v1.0]
 */
public class MongoDBManager<T> {
    private final static MongoDBManager instance = new MongoDBManager();
    private static Mongo mg = null;
    private static com.mongodb.DB db = null;
    /**
     * spring mongodb　集成操作类
     */
    public static MongoTemplate mongoTemplate;

    /**
     * 实例化
     *
     * @return
     * @throws Exception
     */
    public static MongoDBManager getInstance() {
        return instance;
    }

    static {
        try {

            ConfigService configService = new ConfigService();
            configService.init();
            String dbname = configService.getString("mongo.dbname");
            String host = configService.getString("mongo.host");
            String username = configService.getString("mongo.username");
            String password = configService.getString("mongo.password");
            int port = configService.getInt("mongo.port");
            mg = new MongoClient(host, port);
            db = mg.getDB(dbname);
            db.authenticate(username,password.toCharArray());
            UserCredentials userCredentials=new UserCredentials(username,password);
            mongoTemplate = new MongoTemplate(mg, dbname,userCredentials);
        } catch (Exception e) {
            LogUtil.err("MongoDB init failed! because:{}", e.getMessage());
        }
    }
    /**
     * 保存BSON字符串
     * @param bsonStr
     * @param collectionName
     * @throws IOException
     */
    public static boolean saveBsonStr(String bsonStr,String collectionName){
        LogUtil.debug("collectionName={},BsonStr={}",collectionName,bsonStr);
        DBObject dbObject = (DBObject) JSON.parse(bsonStr);
        dbObject.put("send_date",new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        LogUtil.debug("client_id={}",dbObject.get("client_id"));
        DBObject o =new BasicDBObject();
        o.put("client_id",dbObject.get("client_id"));
        o.put("send_date",dbObject.get("send_date"));
        DBObject temp =getCollection(collectionName).findOne(o);
        if(temp!=null){
            temp=getCollection(collectionName).findAndModify(o,dbObject);
            if(temp!=null){
                return true;
            }else {
                return false;
            }
        }else{
            WriteResult result=getCollection(collectionName).save(dbObject);
            if(result.getN()==1){
                return true;
            }else {
                return false;
            }
        }
    }
    /**
     * 添加文件到mongodb
     *
     * @param f
     * @throws java.io.IOException
     */
    public static void uploadFiles(File f, String collectionName) throws IOException {
        GridFS myFS = new GridFS(db, collectionName);
        GridFSInputFile inputFile = myFS.createFile(f);
        inputFile.save();
    }

    /**
     * 根据文件名查找一个文件（返回一个对象）
     *
     * @param collectionName
     * @param filename
     * @return
     */
    public static DBObject findOne(String collectionName, String filename) {
        GridFS myFS = new GridFS(db, collectionName);
        return myFS.findOne(filename);
    }

    /**
     * 查找所有文件列表
     *
     * @param collectionName
     * @return
     */
    public static List<DBObject> getFiles(String collectionName) {
        GridFS myFS = new GridFS(db, collectionName);
        DBCursor cursor = myFS.getFileList();
        return cursor.toArray();
    }

    /**
     * 保存bean
     *
     * @param collection
     */
    public  void save(String collection, T bean) {
        mongoTemplate.save(bean, collection);
    }

    /**
     * 获取集合（表）
     * List<ServerAddress>* @param collection
     */
    public static DBCollection getCollection(String collection) {
        return db.getCollection(collection);
    }
    /**
     * ----------------------------------分割线--------------------------------------
     */
    /**
     * 插入
     *
     * @param collection
     * @param map
     */
    public void insert(String collection, Map<String, Object> map) {
        try {
            DBObject dbObject = map2Obj(map);
            getCollection(collection).insert(dbObject);
        } catch (MongoException e) {
            LogUtil.err("MongoException:{}", e.getMessage());
        }
    }

    public DBObject map2Obj(Map<String, Object> map) {
        DBObject obj = new BasicDBObject();
        obj.putAll(map);
        return obj;
    }

    /**
     * 批量插入
     *
     * @param collection
     * @param list
     */
    public void insertBatch(String collection, List<Map<String, Object>> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        try {
            List<DBObject> listDB = new ArrayList<DBObject>();
            for (int i = 0; i < list.size(); i++) {
                DBObject dbObject = map2Obj(list.get(i));
                listDB.add(dbObject);
            }
            getCollection(collection).insert(listDB);
        } catch (MongoException e) {
            LogUtil.err("MongoException:", e.getMessage());
        }
    }

    /**
     * 删除
     *
     * @param collection
     * @param map
     */
    public void delete(String collection, Map<String, Object> map) {
        DBObject obj = map2Obj(map);
        getCollection(collection).remove(obj);
    }

    /**
     * 删除全部
     *
     * @param collection
     */
    public void deleteAll(String collection) {
        List<DBObject> rs = findAll(collection);
        if (rs != null && !rs.isEmpty()) {
            for (int i = 0; i < rs.size(); i++) {
                getCollection(collection).remove(rs.get(i));
            }
        }
    }

    /**
     * 批量删除
     *
     * @param collection
     * @param list
     */
    public void deleteBatch(String collection, List<Map<String, Object>> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            getCollection(collection).remove(map2Obj(list.get(i)));
        }
    }

    /**
     * 计算满足条件条数
     *
     * @param collection
     * @param map
     */
    public long getCount(String collection, Map<String, Object> map) {
        return getCollection(collection).getCount(map2Obj(map));
    }

    /**
     * 计算集合总条数
     *
     * @param collection
     */
    public long getCount(String collection) {
        return getCollection(collection).find().count();
    }

    /**
     * 更新
     *
     * @param collection
     * @param setFields
     * @param whereFields
     */
    public void update(String collection, Map<String, Object> setFields,
                       Map<String, Object> whereFields) {
        DBObject obj1 = map2Obj(setFields);
        DBObject obj2 = map2Obj(whereFields);
        getCollection(collection).updateMulti(obj1, obj2);
    }

    /**
     * 查找对象（根据主键_id）
     *
     * @param collection
     * @param _id
     */
    public DBObject findById(String collection, String _id) {
        DBObject obj = new BasicDBObject();
        obj.put("_id", ObjectId.massageToObjectId(_id));
        return getCollection(collection).findOne(obj);
    }

    /**
     * 查找集合所有对象
     *
     * @param collection
     */
    public List<DBObject> findAll(String collection) {
        return getCollection(collection).find().toArray();
    }
    /**
     * 查找集合所有对象
     *
     * @param collection
     */
    public DBCursor findAll2(String collection) {
        return getCollection(collection).find();
    }

    /**
     * 查找（返回一个对象）
     *
     * @param map
     * @param collection
     */
    public DBObject findOne(String collection, Map<String, Object> map) {
        DBCollection coll = getCollection(collection);
        return coll.findOne(map2Obj(map));
    }

    /**
     * 查找（返回一个List<DBObject>）
     *
     * @param map
     * @param collection
     * @throws Exception
     */
    public List<DBObject> find(String collection, Map<String, Object> map) throws Exception {
        DBCollection coll = getCollection(collection);
        DBCursor c = coll.find(map2Obj(map));
        if (c != null)
            return c.toArray();
        else
            return null;
    }
}