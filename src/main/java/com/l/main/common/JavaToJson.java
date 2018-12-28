package com.l.main.common;


import com.alibaba.fastjson.JSON;
import com.l.main.board.domain.Weight;

public class JavaToJson {
    //java对象转换成json对象
    public static String jsonStrByJavaBean(Weight weight){
        String jsonStr = JSON.toJSONString(weight);
        System.out.println(jsonStr);
        return jsonStr;
    }
    //json对象转换成json字符串
}
