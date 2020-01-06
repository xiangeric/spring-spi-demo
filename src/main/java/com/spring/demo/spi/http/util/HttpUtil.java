package com.spring.demo.spi.http.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class HttpUtil {

    private static final String UTF_8 = "UTF-8";

    private static final String JSON_CONTENT_TYPE = "application/json";

    public static Object post(String json, String address, Map<String,String> headers,
                              Class<?> returnType, Type type){
        log.info("-------------------------");
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
                    return json2Object(resultJson,returnType,type);
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

    // parse object to json for sending
    public static String object2Json(Object o){
        String json = null;
        if(o !=null){
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                json = objectMapper.writeValueAsString(o);
            } catch (JsonProcessingException e) {
                log.error("error occurs object2Json:",e);
            }
        }
        return json;
    }

    public static Object json2Object(String json,Class<?> returnType,Type type){
        Object result = null;
        if(json!=null && returnType!=null){
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                //如果是容器类型，需要转换，否则会默认LinkedHashMap
                if(isContainClass(returnType) && type!=null && type instanceof ParameterizedType){
                    JavaType javaType = getJavaType(objectMapper,returnType,type);
                    result = objectMapper.readValue(json, javaType);
                }else {
                    result = objectMapper.readValue(json,returnType);
                }
            } catch (Exception e) {
                log.error("error occurs json2Object:",e);
            }
        }

        return result;
    }


    //判断是否为容器类型，容器类型需要对泛型进行处理
    private static boolean isContainClass(Class clazz){
        return List.class.equals(clazz) ||
                Map.class.equals(clazz)||
                Set.class.equals(clazz);
    }

    //获得容器class以及泛型类型，从而得到对应的JavaType
    private static JavaType getJavaType(ObjectMapper objectMapper,
                                        Class<?> containClass, Type type) throws Exception{
        if(type ==null || !(type instanceof ParameterizedType)){
            throw new Exception("type should not be null");
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        //List<User>, 则genericTypes[0] = User.class
        Type[] genericTypes = parameterizedType.getActualTypeArguments();
        Class[] parameterClasses = new Class[genericTypes.length];
        for(int i=0;i<genericTypes.length;i++){
            parameterClasses[i] = (Class) genericTypes[i];
        }
        //构建JavaType
        JavaType javaType = objectMapper.getTypeFactory()
                .constructParametricType(containClass, parameterClasses);
        return javaType;
    }


}
