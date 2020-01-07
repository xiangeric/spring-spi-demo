package com.spring.demo.spi.http.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

@Slf4j
public class HttpUtil {

    private static final String UTF_8 = "UTF-8";

    private static final String JSON_CONTENT_TYPE = "application/json";

    public static Object post(Object postObject, String address, Map<String,String> headers,
                              Class<?> returnType, Type type){
        log.info("-------------------------");
        String json = JSONUtil.object2Json(postObject);
        log.info("-- address: "+ address);
        log.info("-- json: "+json);
        HttpResponse httpResponse = doPost(json, address,headers);
        if(isOk(httpResponse)){
            try {
                String resultJson = EntityUtils.toString(httpResponse.getEntity());
                log.info("-- response:"+resultJson);
                //如果方法不为void类型，尝试解析响应json字符串
                if(returnType!=null && !Void.TYPE.equals(returnType)){
                    //如果是String类型，则直接返回
                    if(String.class.equals(returnType)){
                        return resultJson;
                    }
                    return JSONUtil.json2Object(resultJson,returnType,type);
                }
            } catch (IOException e) {
                log.error("resolve response error:",e);
            }
        }
        log.info("-------------------------");
        return null;
    }


    //建立http client，发送http请求，返回响应
    public static HttpResponse doPost(String json, String address, Map<String,String> map){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost postMethod = new HttpPost(address);
        try {
            //如果有请求参数
            if(!StringUtils.isEmpty(json)){
                StringEntity entity = new StringEntity(json);
                entity.setContentEncoding(UTF_8);
                entity.setContentType(JSON_CONTENT_TYPE);
                postMethod.setEntity(entity);
            }
            //添加header
            if(map!=null && !map.isEmpty()){
                map.forEach((k,v) ->{
                    postMethod.addHeader(k,v);
                });

            }
            return httpclient.execute(postMethod);
        } catch (Exception e) {
            log.error("error occur when execute http post method:",e);
        }
        return null;
    }

    //判断响应状态是否为200
    public static boolean isOk(HttpResponse httpResponse){
        if(httpResponse != null){
            if(httpResponse.getStatusLine() != null
                    && httpResponse.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
                return true;

            }
            log.error("http response's status code is :"+ httpResponse.getStatusLine().getStatusCode());
        }else{
            log.error("http response is null");
        }
        return false;
    }
}
