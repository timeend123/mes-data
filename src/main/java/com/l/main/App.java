package com.l.main;

import com.l.main.board.domain.Weight;
import com.l.main.board.service.ShowPackageBoxSum;
import com.l.main.board.serviceImpl.PackageBoxSum;
import com.l.main.common.JavaToJson;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;

public class App {
    public static void main(String[] args){
        /*Vertx.vertx().createHttpServer().requestHandler(req -> req.response()
                .end("<h1>ddddddddddddddd<h1>")).listen(8080);*/
        //vertx对象
        Vertx vertx = Vertx.vertx();
        //创建httpsever
        HttpServer server = vertx.createHttpServer();
        //创建路由对象
        Router router = Router.router(vertx);
            //不同的请求建立不同的路由
            /*router.route("/hello")
                    .handler(RouterContext -> {
                        HttpServerResponse response = RouterContext.response();
                        response.setChunked(true);
                        response.write("<h1>Hello World!!<h1>").end();
                    });*/
            /*router.route("/test").handler(req ->
                req.response().end("test")
            );*/

            router.route("/test1").handler(routingContext -> {
                /*String param = routingContext.request().getParam("name");
                String param1 = routingContext.request().getParam("idea");
                HttpServerResponse response  = routingContext.response();
                System.out.println("param: "+param+"param1: "+param1);
                response.setChunked(true).write("name:" + param + "idea:" + param1).end();*/

                //获得前端参数
                /*String startTime = routingContext.request().getParam("");
                String endTime = routingContext.request().getParam("");*/
                /*String startTime = "2018-11-30 0:0:0";
                String endTime = "2018-11-31 23:00:00";
                ShowPackageBoxSum sum = new PackageBoxSum();
                Weight w = sum.show_PackageBoxSum(startTime,endTime);*/
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Weight w = new Weight();
//                String jsonStr = JavaToJson.jsonStrByJavaBean(w);
                String jsonStr = Json.encode(w);
                routingContext.response().setChunked(true).write(jsonStr).end();

            });

            //router.route("/todo").handler(req -> req.response().end("<h1>do something!!!<h1>"));
            //将请求交给路由处理
            server.requestHandler(router::accept);
            //服务器监听端口
            server.listen(8080);

    }
}

