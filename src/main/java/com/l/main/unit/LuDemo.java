package com.l.main.unit;

import com.l.main.common.MongoConnect;
import com.mongodb.DBCursor;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.*;

public class LuDemo {

    private static final String INDEX_PATH = "D:/index";

    //private static final List<File> files = new ArrayList<File>();

    //创建索引
    public void createIndex(){

        File[] files = new File(INDEX_PATH).listFiles();


        String content = "";
        //打开索引目录
        try {
            FSDirectory fs = FSDirectory.open(new File(INDEX_PATH));
            //创建indexWrite
            IndexWriterConfig cfg = new IndexWriterConfig(Version.LUCENE_45,new StandardAnalyzer(Version.LUCENE_45));
            //新建一个索引
            IndexWriter writer = new IndexWriter(fs,cfg);


            for(File f : files){

                content += txt2String(f);

                Document document = new Document();
                //添加索引，但是不分析
                document.add(new TextField("path",f.getPath(), Field.Store.YES));
                //索引的名字储存
                document.add(new TextField("name",f.getName(), Field.Store.YES));
                //文档内容
                document.add(new TextField("content",content,Field.Store.YES));
                //添加文档到索引中
                writer.addDocument(document);
                System.out.println("path:"+f.getPath());

                content = "";
            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("打开索引目录失败");
        }
    }
    //按关键字搜索文件
    public void searchFile(String text){

        try {
            //打开索引目录
            FSDirectory fs = FSDirectory.open(new File(INDEX_PATH));
            System.out.println("1");
            //获得Indexreader对象
            IndexReader reader = IndexReader.open(fs);
            System.out.println("2");
            //根据Indexreader获得IndexSearcher对象
            IndexSearcher searcher = new IndexSearcher(reader);
            System.out.println("3");
            //创建搜素条件对象Query
              //创建parser  确定搜索文件的内容，就是搜索文件的哪一部分
            QueryParser parser = new QueryParser(Version.LUCENE_45,"content",new StandardAnalyzer(Version.LUCENE_45));
            System.out.println("4");
              //创建Query
            try {
                Query query = parser.parse(text);
                System.out.println("5");
                //根据search，搜索返回TopDose ,10表示查询10条
                TopDocs docs = searcher.search(query,10);
                System.out.println("6");
                System.out.println("docs:"+docs.totalHits);
                //根据TopDocs，获取SocreDoc
                for (ScoreDoc result : docs.scoreDocs){
                    System.out.println("run...");
                    //根据id获取document
                    Document doc = searcher.doc(result.doc);

                    //获取索引的文件名称
                    System.out.println(doc.get("name")+","+doc.get("path"));
                    System.out.println("run2...");
                }
            } catch (ParseException e) {
                e.printStackTrace();
                System.out.println("解析失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("搜索失败");
        }
    }

    public String txt2String(File file) throws IOException {
        FileInputStream fis ;
        BufferedReader bf = null;
        StringBuilder sb = null;
        try {
            fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            //将file文件内容转成字符串
            bf = new BufferedReader(isr);
            String content = "";
            sb = new StringBuilder();
            while (content != null) {
                content = bf.readLine();
                if (content == null) {
                    break;
                }
                sb.append(content.trim());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            bf.close();
        }

        return sb.toString();

    }


}
