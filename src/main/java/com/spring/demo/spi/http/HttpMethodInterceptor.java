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
            HttpMethod httpMethod = method.getDeclaredAnnotation(HttpMethod.class);
            String subAddress = String.format(address,httpMethod.value());
            Map<String,String> headerMap = null;
            final HttpHeader[] headers = httpMethod.headers();
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
        if(parameters!=null && parameters.length>0){
            json = HttpUtil.object2Json(parameters[0]);
        }
        result = HttpUtil.post(json,cache.address,cache.headers,
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
