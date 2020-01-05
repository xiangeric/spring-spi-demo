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
                if(returnType!=null){
                    return json2Object(resultJson,returnType,type);
                }
            } catch (IOException e) {
                log.error("resolve response error:",e);
            }
        }
        log.info("-------------------------");
        return null;
    }


    public static HttpResponse doPost(String json, String address, Map<String,String> map){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost postMethod = new HttpPost(address);
        try {
            if(!StringUtils.isEmpty(json)){
                StringEntity entity = new StringEntity(json);
                entity.setContentEncoding(UTF_8);
                entity.setContentType(JSON_CONTENT_TYPE);
                postMethod.setEntity(entity);
            }
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


    private static boolean isContainClass(Class clazz){
        return List.class.equals(clazz) ||
                Map.class.equals(clazz)||
                Set.class.equals(clazz);
    }

    private static JavaType getJavaType(ObjectMapper objectMapper,
                                        Class<?> containClass, Type type) throws Exception{
        if(type ==null || !(type instanceof ParameterizedType)){
            throw new Exception("type should not be null");
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] genericTypes = parameterizedType.getActualTypeArguments();
        Class[] parameterClasses = new Class[genericTypes.length];
        for(int i=0;i<genericTypes.length;i++){
            parameterClasses[i] = (Class) genericTypes[i];
        }
        JavaType javaType = objectMapper.getTypeFactory()
                .constructParametricType(containClass, parameterClasses);
        return javaType;
    }


}
