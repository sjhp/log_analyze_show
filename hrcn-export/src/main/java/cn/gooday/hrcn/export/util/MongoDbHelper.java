package cn.gooday.hrcn.export.util;

import com.mongodb.*;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [mongodb工具类]
 *
 * @ProjectName: [common]
 * @Author: [Jon.K]
 * @CreateDate: [2015/2/10]
 * @Update: [说明本次修改内容] BY[][2015/2/10]
 * @Version: [v1.0]
 */
public class MongoDbHelper {
    protected static Logger logger = LoggerFactory.getLogger("MongoDbHelper");
    private Mongo myMongo = null;
    private DB myDb = null;
    private static final String serverAddr="127.0.0.1";
    private static final int port=27017;
    private static final String myDbName="test";
    private static final String userName="admin";
    private static final String passWord="123456";
    private static Map<String, DBCollection> dbCollectionMap = new ConcurrentHashMap<String, DBCollection>();

    public MongoDbHelper(){
        this.init();
    }

    /**
     * 初始化
     */
    private void init() {
        if(myMongo==null){
            try {
                this.myMongo=new MongoClient(serverAddr,port);
                if(this.myMongo!=null){
                    this.myDb=this.myMongo.getDB(myDbName);
                    if(userName!=null){
                        this.myDb=this.myMongo.getDB("admin");
                        boolean flag=this.myDb.authenticate(userName,passWord.toCharArray());
                        if(flag)this.myDb=this.myMongo.getDB(myDbName);
                        else throw new RuntimeException("authenticate failed! username:"+userName+",passWord:"+passWord);
                    }
                }
            }catch (UnknownHostException e){
                logger.error("连接mongoDb失败, 服务器地址: " + MongoDbHelper.serverAddr + ", 端口: " + MongoDbHelper.port);
                throw new RuntimeException(e);
            }catch (RuntimeException e){
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 获取集合（表）
     * @param collectionName
     * @return
     */
    private DBCollection getDBCollection(String collectionName) {
        DBCollection collection = null;
        if(dbCollectionMap.containsKey(collectionName)){
            return dbCollectionMap.get(collectionName);
        }else{
            collection=this.myDb.getCollection(collectionName);
            if(collection!=null){
                dbCollectionMap.put(collectionName,collection);
            }
        }
        return collection;
    }

    /**
     *判断对象是否存在
     * @param collectionName
     * @param query
     * @return
     */
    public boolean isDocumentExsit(String collectionName, DBObject query) {
        boolean result=false;
        DBCursor cursor=null;
        DBCollection collection =this.getDBCollection(collectionName);
        if(collection!=null){
            cursor=collection.find(query);
        }
        if(cursor!=null && cursor.hasNext()){
            result= true;
        }
        return result;
    }

    /**
     * 查询
     * @param collectionName
     * @param query
     * @return
     */
    public DBCursor selectDocument(String collectionName, DBObject query) {
        DBCursor result=null;
        DBCollection collection =this.getDBCollection(collectionName);
        if(collection!=null){
            result=collection.find(query);
        }
        return result;
    }

    /**
     * 插入
     * @param collectionName
     * @param newDocument
     */
    public void insertDocument(String collectionName, DBObject newDocument) {
        DBCollection collection =this.getDBCollection(collectionName);
        if(collection!=null && isDocumentExsit(collectionName,newDocument)){
            collection.insert(newDocument);
        }
    }

    /**
     * 更新
     * @param collectionName
     * @param query
     * @param updatedDocument
     * @return
     */
    public boolean updateDocument(String collectionName, DBObject query, DBObject updatedDocument) {
        boolean result=false;
        DBCollection collection =this.getDBCollection(collectionName);
        if(collection!=null){
            WriteResult writeResult=collection.update(query,updatedDocument);
            if(writeResult!=null &&writeResult.getN()>0){
                result=true;
            }
        }
        return result;
    }

    /**
     * 保存
     * @param collectionName
     * @param saveObject
     * @return
     */
    public boolean saveDoc(String collectionName,DBObject saveObject){
        boolean result=false;
        DBCollection collection =this.getDBCollection(collectionName);
        if(collection!=null){
            WriteResult writeResult=collection.save(saveObject);
            if(writeResult!=null &&writeResult.getN()>0){
                result=true;
            }
        }
        return result;
    }

    /**
     * 删除
     * @param collectionName
     * @param query
     * @return
     */
    public boolean deleteDocument(String collectionName, DBObject query) {
        boolean result=false;
        DBCollection collection =this.getDBCollection(collectionName);
        if(collection!=null){
            WriteResult writeResult=collection.remove(query);
            if(writeResult!=null &&writeResult.getN()>0){
                result=true;
            }
        }
        return result;
    }
    /**
     * 添加文件到mongodb
     * @param f
     * @throws java.io.IOException
     */
    public  void uploadFiles(File f) throws IOException {
        if(myDb==null){
            throw new RuntimeException("db is null! ");
        }
        GridFS myFS = new GridFS(myDb);
        GridFSInputFile inputFile = myFS.createFile(f);
        inputFile.save();
    }
    public  List<DBObject> getFiles(){
        if(myDb==null){
            throw new RuntimeException("db is null! ");
        }
        GridFS myFS = new GridFS(myDb);
        DBCursor cursor = myFS.getFileList();
        return cursor.toArray();
    }
}
