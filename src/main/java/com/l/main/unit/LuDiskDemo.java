package com.l.main.unit;

import com.l.main.common.MongoConnect;
import com.mongodb.client.MongoCursor;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.KeepOnlyLastCommitDeletionPolicy;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;

public class LuDiskDemo {


    private static RAMDirectory ramDirectory;

    private static IndexWriter ramWriter;

    private static IndexReader ramReader;


    //创建索引
    public static void createIndex(String startDate,String endDate) throws IOException {



        int storeCount = 0;
        long s = System.currentTimeMillis();
        try {
            ramDirectory = new RAMDirectory();

            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_45,new StandardAnalyzer(Version.LUCENE_45));
            indexWriterConfig.setIndexDeletionPolicy(new KeepOnlyLastCommitDeletionPolicy());
            indexWriterConfig.setRAMBufferSizeMB(5120).setMaxBufferedDocs(360000);

            ramWriter = new IndexWriter(ramDirectory,indexWriterConfig);
            //获得mongo数据库指定集合中的数据
            //DBCursor dbCursor = MongoConnect.getDBCursor();

            //获得mongo数据库指定集合中的数据(正式库)
            long st = System.currentTimeMillis();
            MongoCursor<org.bson.Document> dbCursor = MongoConnect.getMongoCursor(startDate,endDate);

            long et = System.currentTimeMillis();
            System.out.println("databasecost: "+(et-st));

            //Document document = new Document();
            List<Document> ds = new ArrayList<>();
            if (dbCursor.hasNext()) {
                while (dbCursor.hasNext()) {

                    Document document = new Document();
                    org.bson.Document docObject = dbCursor.next();
                    //System.out.println("cdt:"+sdf.format(dbObject.get("cdt")));
                    //添加索引，文档id储存
                    document.add(new TextField("_id",docObject.get("_id").toString() , Field.Store.YES));

                    //索引的净重储存
                    document.add(new TextField("netWeight", docObject.get("netWeight") == null?"0":docObject.get("netWeight").toString(), Field.Store.YES));
                    //索引的文档创建的时间储存
                    document.add(new TextField("grade", docObject.get("grade") == null?"nothing":docObject.get("grade").toString(), Field.Store.YES));
                    //公共标志
                    document.add(new TextField("content", "index", Field.Store.YES));
                    //文档内容
                    //document.add(new TextField("content",content,Field.Store.YES));
                    //添加文档到索引中

                    ds.add(document);

                    /*ramWriter.addDocument(document);
                    ramWriter.commit();
                    storeCount += 1;
                    document.removeField("_id");
                    document.removeField("netWeight");
                    document.removeField("grade");
                    document.removeField("content");*/

                    storeCount += 1;
                }
            }
            ramWriter.addDocuments(ds);
            ramWriter.commit();
            long e = System.currentTimeMillis();
            System.out.println("制作索引时间："+(e-s));
            System.out.println("索引文档内容数量："+storeCount+"条");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("打开索引目录失败");
        }
    }

    //按关键字搜索文件
    public static List<Document> searchFile(String text) throws IOException {


        List<Document> documents = new ArrayList<Document>();
        //IndexReader ramReader = null;
        long start = System.currentTimeMillis();
        try {
                //打开索引目录
                //ramDirectory = new RAMDirectory();
                ramReader = IndexReader.open(ramDirectory);
                //根据Indexreader获得IndexSearcher对象
                IndexSearcher searcher = new IndexSearcher(ramReader);
                //创建搜素条件对象Query
                //创建parser  确定搜索文件的内容，就是搜索文件的哪一部分
                /*QueryParser parser = new QueryParser(Version.LUCENE_45, "cdt", new StandardAnalyzer(Version.LUCENE_45));

                //创建Query

                 Query query = parser.parse(text);*/
                Query query = null;
                if ("".equals(text)){
                    QueryParser parser = new QueryParser(Version.LUCENE_45,"content",new StandardAnalyzer(Version.LUCENE_45));
                    query = parser.parse("index");
                }else {
                    QueryParser parser = new QueryParser(Version.LUCENE_45,text,new StandardAnalyzer(Version.LUCENE_45));
                    query = parser.parse("1770980569354600486");
                }

                 //根据search，搜索返回TopDose ,10表示查询10条
                 TopDocs docs = searcher.search(query, 100000);
                 System.out.println("docs:" + docs.totalHits);
                 ScoreDoc[] s = docs.scoreDocs;
                 //根据TopDocs，获取SocreDoc
                for (ScoreDoc result : docs.scoreDocs) {
                    //System.out.println("running...");
                    //根据id获取document
                    Document doc = searcher.doc(result.doc);

                    documents.add(doc);
                    //获取索引的文件名称
                    /*System.out.println(doc.get("name") + "," + doc.get("path"));
                    System.out.println("running...");*/
                }

                long e = System.currentTimeMillis();
                System.out.println("查找索引文件时间: "+(e-start));
                System.out.println("查到的结果数量:"+documents.size());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("搜索失败");
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("解析失败");
        }

        return documents;
    }

    //清除索引文件
    /*public static void clearIndex(){
        File f = new File(INDEX_PATH);
        File[] files = f.listFiles();
        if (files.length != 0){
            for (File file : files){
                file.delete();
            }
        }
    }*/

    //释放资源
    public static void clearResource(){

        try {
            if (ramReader != null) {
                ramReader.close();
            }
            if (ramWriter != null){
                ramWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (ramDirectory != null){
            ramDirectory.close();
        }
    }

}
