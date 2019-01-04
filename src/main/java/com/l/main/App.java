package com.l.main;

import com.l.main.board.domain.Weight;
import com.l.main.board.service.ShowPackageBoxSum;
import com.l.main.board.serviceImpl.PackageBoxSum;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class App extends AbstractVerticle {
    public static void main(String[] args){


        //设置主线程阻塞时间
        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setBlockedThreadCheckInterval(9999999);
        vertxOptions.setWorkerPoolSize(100);
        Vertx vertx = Vertx.vertx(vertxOptions);

        vertx.deployVerticle(new App());


    }

    //部署服务
    @Override
    public void start() {

        //实例化一个路由，用来路由不同的接口
        Router router = Router.router(vertx);

        //增加一个处理器，将请求的上下文信息放入RoutingContext对象中
        router.route().handler(BodyHandler.create());



        //处理post方法的路由
        router.post("/getData").handler(this::handlePost);

        //创建httpserver，分发路由，监听8080端口
        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }

    private void handlePost(RoutingContext routingContext){
        JsonObject j1 =routingContext.getBodyAsJson();
        String startTime = j1.getString("startTime");
        String endTime = j1.getString("endTime");

        if (isBlank(startTime) || isBlank(endTime)){
            routingContext.response().setStatusCode(400).end();
        }

        ShowPackageBoxSum s = new PackageBoxSum();
        Weight w = s.show_PackageBoxSum(startTime,endTime);
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("净重",w.getWeight()).put("双a率",w.getDoubleARate());
        routingContext.response().putHeader("content-type","application/json")
                .end(jsonObject.encodePrettily());

    }

    private boolean isBlank(String str){

        if (str == null || "".equals(str)){
            return true;
        }
        return false;
    }

}

