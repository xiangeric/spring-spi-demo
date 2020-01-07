package com.spring.demo.spi.http;

import com.spring.demo.spi.http.anno.HttpHeader;
import com.spring.demo.spi.http.anno.HttpMethod;
import com.spring.demo.spi.http.util.HttpUtil;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class HttpMethodInterceptor implements MethodInterceptor {

    private final String address;

    private Map<Method,MethodCache> caches = new ConcurrentHashMap<>(8);

    public HttpMethodInterceptor(String address){
        if(!address.endsWith("/")){
            address = address + "/";
        }
        address = address + "%s";
        this.address = address;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] parameters, MethodProxy methodProxy) throws Throwable {
        //如果是equals()、hashcode()、toString()则直接调用
        if(ReflectionUtils.isEqualsMethod(method) ||
                ReflectionUtils.isHashCodeMethod(method) ||
                ReflectionUtils.isToStringMethod(method) ){
            return methodProxy.invokeSuper(o,parameters);
        }
        Object result;
        MethodCache cache;
        if(caches.containsKey(method)){
            cache = caches.get(method);
        }else{
            //获得@HttpMethod
            HttpMethod httpMethod = method.getDeclaredAnnotation(HttpMethod.class);
            //获得全地址
            String subAddress = String.format(address,httpMethod.value());
            Map<String,String> headerMap = null;
            //获得Header信息
            final HttpHeader[] headers = httpMethod.headers();
            //存储Header信息
            if(headers!=null && headers.length>0){
                headerMap = new HashMap<>(8);
                for(HttpHeader httpHeader:headers){
                    headerMap.put(httpHeader.name(),httpHeader.value());
                }
            }
            cache = new MethodCache(subAddress,headerMap);
            caches.put(method,cache);
        }

        String json = null;
        //如果有参数，则取第一个参数作为请求参数
        Object postObject = null;
        if(parameters!=null && parameters.length>0){
            postObject = parameters[0];
        }
        //调用http工具请求接口，这里同时获得方法返回类型以及返回类型中可能
        //存在泛型情况，如List<User>,那么User为泛型信息
        result = HttpUtil.post(postObject,cache.address,cache.headers,
                method.getReturnType(),method.getGenericReturnType());
        return result;
    }

    @NoArgsConstructor
    static class MethodCache{
        //指定发送http的地址
        private String address;

        private Map<String,String> headers;

        public MethodCache(String address,Map<String,String> headers){
            this.address = address;
            this.headers = headers;
        }
    }
}
