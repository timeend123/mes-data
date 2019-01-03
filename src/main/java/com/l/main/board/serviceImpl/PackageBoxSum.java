package com.l.main.board.serviceImpl;


import com.l.main.board.domain.Weight;
import com.l.main.board.service.ShowPackageBoxSum;

import com.l.main.unit.LuDiskDemo;

import org.apache.lucene.document.Document;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;


public class PackageBoxSum implements ShowPackageBoxSum {
   /* private static final String MONGO_HOST = "192.168.0.38";
    private static final Integer MONGO_PORT = 27017;
    private static final String MONGO_DB_NAME = "mes-auto";
    //private static final String MONGO_USERNAME = "mes-auto";
    //private static final String MONGO_PASSWORD = "mes-auto-mongo@com.hengyi.japp";
    private static final String MONGO_COLLECTION_NAME = "T_PackageBox";*/
    /*public double show_packageboxsum() throws UnknownHostException, ParseException {
        // 获取Mongo客户端
        MongoClient mongoClient = new MongoClient(MONGO_HOST, MONGO_PORT);
        *//**
         * 2.获取到指定db（若不存在，则mongo会创建该db）
         *//*
        DB db = mongoClient.getDB(MONGO_DB_NAME);
        //2.1用户名&密码校验
//		boolean auth = db.authenticate(null,null);
//		if (!auth) {
//			System.out.println(MONGO_DB_NAME + "连接失败！");
//			return 0;
//		}
//		System.out.println(MONGO_DB_NAME + "连接成功！");

        // 2.2获取该db下所有集合名称并打印
        Set<String> collections = db.getCollectionNames();
//        for (String collection : collections) {
//            System.out.println(collection);
//        }

        // 2.3获取指定集合(若不存在，则mongo会创建该集合)
        DBCollection collection = db.getCollection(MONGO_COLLECTION_NAME);
        // 3.2查询一条文档
        *//*BasicDBObject searchObj = new BasicDBObject();
        String startDate = "2018-11-01 00:00:00";
        String endDate = "2018-12-10 19:55:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        searchObj.put("$gte", sdf.parse(startDate));
        searchObj.put("$lte",  sdf.parse(endDate));
        BasicDBObject ageCompare = new BasicDBObject();
        ageCompare.put("cdt",searchObj);*//*
        //searchObj.put("name", "金赵波");
        //DBCursor cursor = collection.find(ageCompare);

        DBCursor cursor = collection.find();
        double sum=0;
        int count = 0;
        if (cursor.hasNext()) {
            while (cursor.hasNext()) {
               // System.out.println(cursor.next());
                count += 1;
                DBObject dbObj= cursor.next();
                if(dbObj.get("netWeight")!=null){
                    sum=sum+Double.parseDouble(dbObj.get("netWeight").toString());
                }
            }
        } else {
            System.out.println("没有数据!!!");
        }
        sum=sum/1000;
        return sum;
    }*/

    public Weight show_PackageBoxSum(String startDate, String endDate) {

        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //String today = sdf.format(new Date());
        //String date = "2018-11-29";

        //净重总值
        double netWeight = 0.0;
        //指定时间内的所有记录数量
        int allDocs = 0;
        //aa级记录数
        float aaDocs = 0;
        //双a率
        float rate = 0;
        DecimalFormat df = new DecimalFormat("0.00");
        //创建索引
        try {
            LuDiskDemo.createIndex(startDate,endDate);
            //查找索引文件所有的文档记录,得到净重
            List<Document> documents = LuDiskDemo.searchFile("");
            if (documents.size() == 0){
                Weight w = new Weight();
                w.setWeight(0);
                w.setDoubleARate("0");
                return w;
            }

            allDocs = documents.size();

            for (Document result : documents){

                netWeight += Double.parseDouble(result.get("netWeight"));

            }

            //查找结果文档中所有aa等级的记录数
            List<Document> aaDocuments = LuDiskDemo.searchFile("grade");
            aaDocs = (float) aaDocuments.size();

            rate = aaDocs/allDocs;
            //释放资源
            LuDiskDemo.clearResource();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Weight w = new Weight();
        w.setWeight(netWeight);
        w.setDoubleARate(df.format(rate));
        return w;
    }
}
