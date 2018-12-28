package com.l.main.unit;

import com.l.main.common.MongoConnect;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
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
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.List;

public class LuDiskDemo {

    private static final String INDEX_PATH = "D:\\index\\mongtest";

    private static RAMDirectory ramDirectory;

    private static IndexWriter ramWriter;


    //内存索引
    /*static {

        try {
            FSDirectory fsDirectory = FSDirectory.open(new File(INDEX_PATH));
            LuDiskDemo.ramDirectory = new RAMDirectory(fsDirectory, IOContext.READONCE);
            fsDirectory.close();
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_45,new StandardAnalyzer(Version.LUCENE_45));
            indexWriterConfig.setIndexDeletionPolicy(new KeepOnlyLastCommitDeletionPolicy());

            ramWriter = new IndexWriter(ramDirectory,indexWriterConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }*/

    //创建索引
    public static void createIndex() throws IOException {


        //删除现有的索引文档
        LuDiskDemo.clearIndex();
        FSDirectory fs = null;
        IndexWriter writer = null;
        int storeCount = 0;
        try {
            fs = FSDirectory.open(new File(INDEX_PATH));
            //创建indexWrite
            IndexWriterConfig cfg = new IndexWriterConfig(Version.LUCENE_45,new StandardAnalyzer(Version.LUCENE_45));
            //新建一个索引
            writer = new IndexWriter(fs,cfg);

            //获得mongo数据库指定集合中的数据
            DBCursor dbCursor = MongoConnect.getDBCursor();

            System.out.println("netWeight不为null且存在的数量："+dbCursor.size());

            if (dbCursor.hasNext()) {
                while (dbCursor.hasNext()) {

                    Document document = new Document();
                    DBObject dbObject = dbCursor.next();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    //System.out.println("cdt:"+sdf.format(dbObject.get("cdt")));
                    //添加索引，文档id储存
                    document.add(new TextField("_id",dbObject.get("_id").toString() , Field.Store.YES));
                    //索引的净重储存
                    document.add(new TextField("netWeight", dbObject.get("netWeight").toString(), Field.Store.YES));
                    //索引的文档创建的时间储存
                    document.add(new TextField("cdt", sdf.format(dbObject.get("cdt")).substring(0,9), Field.Store.YES));
                    //文档内容
                    //document.add(new TextField("content",content,Field.Store.YES));
                    //添加文档到索引中
                    writer.addDocument(document);
                    storeCount += 1;

                }
            }
            System.out.println("索引文档内容数量："+storeCount+"条");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("打开索引目录失败");
        }finally {
            if (writer != null){
                writer.close();
            }
            if (fs != null){
                fs.close();
            }
        }
    }

    //按关键字搜索文件
    public static List<Document> searchFile(String text) throws IOException {


        List<Document> documents = new ArrayList<Document>();
        FSDirectory fs = null;
        IndexReader reader = null;
        try {
                //打开索引目录
                fs = FSDirectory.open(new File(INDEX_PATH));
                System.out.println("1");
                //获得Indexreader对象
                reader = IndexReader.open(fs);
                //reader = IndexReader.open(ramDirectory);
                System.out.println("2");
                //根据Indexreader获得IndexSearcher对象
                IndexSearcher searcher = new IndexSearcher(reader);
                System.out.println("3");
                //创建搜素条件对象Query
                //创建parser  确定搜索文件的内容，就是搜索文件的哪一部分
                QueryParser parser = new QueryParser(Version.LUCENE_45, "cdt", new StandardAnalyzer(Version.LUCENE_45));
                System.out.println("4");
                //创建Query

                 Query query = parser.parse(text);
                 System.out.println("5");
                 //根据search，搜索返回TopDose ,10表示查询10条
                 TopDocs docs = searcher.search(query, 100000);
                 System.out.println("6");
                 System.out.println("docs:" + docs.totalHits);
                 ScoreDoc[] s = docs.scoreDocs;
                 //根据TopDocs，获取SocreDoc
                for (ScoreDoc result : docs.scoreDocs) {
                    System.out.println("running...");
                    //根据id获取document
                    Document doc = searcher.doc(result.doc);

                    documents.add(doc);
                    //获取索引的文件名称
                    /*System.out.println(doc.get("name") + "," + doc.get("path"));
                    System.out.println("running...");*/
                }

                System.out.println("查到的结果数量:"+documents.size());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("搜索失败");
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("解析失败");
        }finally {
            if (reader != null){
                reader.close();
            }
            if (fs != null){
                fs.close();
            }
        }

        return documents;
    }

    //清除索引文件
    public static void clearIndex(){
        File f = new File(INDEX_PATH);
        File[] files = f.listFiles();
        if (files.length != 0){
            for (File file : files){
                file.delete();
            }
        }
    }

}
