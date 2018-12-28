package com.l.main.common;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 特别注意：
 * mongodb开启权限认证启动后，若使用本测试类一直报Unauthorized认证失败错，
 * 别忘了修改mongodb的默认认证机制为MONGODB-CR
 * 具体请看我的另一篇博文：https://blog.csdn.net/sanpangouba/article/details/78953556
 *  *
 *  * @author Cheung
 * @date 2017/12/29
 */
public class MongoConnect {

	private static final String MONGO_HOST = "192.168.0.38";
	private static final Integer MONGO_PORT = 27017;
	private static final String MONGO_DB_NAME = "mes-auto";
	//private static final String MONGO_USERNAME = "mes-auto";
	//private static final String MONGO_PASSWORD = "mes-auto-mongo@com.hengyi.japp";
	private static final String MONGO_COLLECTION_NAME = "T_Grade";


	/*public static void main(String[] args) throws UnknownHostException {

		// 获取Mongo客户端
		MongoClient mongoClient = new MongoClient(MONGO_HOST, MONGO_PORT);
		*//**
		 * 1.获取所有db名称并打印
		 * （注意：在MongoDB未开启auth权限认证下可用，若已经开启权限校验，则会报Unauthorized错）
		 *//*
		*//*List<String> dbs = mongoClient.getDatabaseNames();
		System.out.println(MONGO_HOST + ":" + MONGO_PORT.toString() + "包含如下数据库：");
		for (String db : dbs) {
			System.out.println(db);
		}*//*

		*//**
		 * 2.获取到指定db（若不存在，则mongo会创建该db）
		 *//*
		DB db = mongoClient.getDB(MONGO_DB_NAME);
		// 2.1用户名&密码校验
//		boolean auth = db.authenticate(null,null);
//		if (!auth) {
//			System.out.println(MONGO_DB_NAME + "连接失败！");
//			return;
//		}
//		System.out.println(MONGO_DB_NAME + "连接成功！");

		// 2.2获取该db下所有集合名称并打印
		Set<String> collections = db.getCollectionNames();
		System.out.println(db.getName() + "包含如下集合：");
		for (String collection : collections) {
			System.out.println(collection);
		}

		// 2.3获取指定集合(若不存在，则mongo会创建该集合)
		DBCollection collection = db.getCollection(MONGO_COLLECTION_NAME);




		// 3.2查询一条文档
		//BasicDBObject searchObj = new BasicDBObject();
		//searchObj.put("name", "金赵波");
		//DBCursor cursor = collection.find(searchObj);
		DBCursor cursor = collection.find();


	}*/

	//获取Mongo数据库指定集合所有的数据
	public static DBCursor getDBCursor(){
		// 获取Mongo客户端
		MongoClient mongoClient = null;
		mongoClient = new MongoClient(MONGO_HOST, MONGO_PORT);

		/**
		 * 2.获取到指定db（若不存在，则mongo会创建该db）
		 */
		DB db = mongoClient.getDB(MONGO_DB_NAME);

		// 2.3获取指定集合(若不存在，则mongo会创建该集合)
		DBCollection collection = db.getCollection(MONGO_COLLECTION_NAME);

		BasicDBObject searchObjN = new BasicDBObject();
		//查找净重不为null的数据
		searchObjN.put("$ne", null);
		searchObjN.put("$exists",true);
		BasicDBObject searchObjR = new BasicDBObject();
		searchObjR.put("netWeight",searchObjN);
		DBCursor cursor = collection.find(searchObjR);
		return cursor;
	}


	public static void main(String[] args) throws UnknownHostException {

		final ServerAddress serverAddress = new ServerAddress("10.2.0.212", 27017);
		List<ServerAddress> addrs = new ArrayList<>();
		addrs.add(serverAddress);
		final String db_name = "mes-auto";
		final String username = "mes-auto";
		final String authSource = "admin";
		final String password = "mes-auto-mongo@com.hengyi.japp";

		final MongoCredential mongoCredential = MongoCredential
				.createScramSha1Credential(username,authSource,password.toCharArray());
		List<MongoCredential> credentials = new ArrayList<>();
		credentials.add(mongoCredential);

		MongoClient mongoClient = new MongoClient(addrs,credentials);
		MongoDatabase mongoDatabase = mongoClient.getDatabase(db_name);
		MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(MONGO_COLLECTION_NAME);


		System.out.println("!!!!!!");


		//System.out.println("集合内文档数量： "+mongoCollection.count());
		//long start = System.currentTimeMillis();
		//MongoCursor cursor = mongoCollection.find().iterator();
		//MongoCursor<Document> cursor = mongoCollection.find().iterator();
		MongoCursor<Document> cursor = mongoCollection.find().iterator();
		int count = 0;
		while (cursor.hasNext()){
			System.out.println(cursor.next().toJson());
			count += 1;
			System.out.println("count: "+ count);
		}
		/*int count = 0;
		if (cursor.hasNext()){
			Document document = cursor.next();
		}
		long end = System.currentTimeMillis();
		double cost = (end - start)/1000;
		System.out.println("count: "+count+"cost: "+cost);*/

	}

}
