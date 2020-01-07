package com.spring.demo.spi.http.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

//基于Jackson的JSON转换工具
@Slf4j
public class JSONUtil {

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
