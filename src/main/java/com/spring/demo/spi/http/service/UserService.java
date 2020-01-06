package com.spring.demo.spi.http.service;

import com.spring.demo.spi.entity.User;
import com.spring.demo.spi.http.anno.HttpHeader;
import com.spring.demo.spi.http.anno.HttpMethod;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserService {

    //没有参数以及返回值
    @HttpMethod(value = "call",headers = {@HttpHeader(name = "secure", value = "true")})
    public void call();

    //有参数但没有返回值
    @HttpMethod(value = "addUser")
    public void addUser(User user);

    //有参数，对象返回值
    @HttpMethod(value = "getUser",headers = {@HttpHeader(name = "secure", value = "true")})
    public User getUser(String name);

    //没有参数，容器List返回值
    @HttpMethod(value = "getAllUsers",headers = {@HttpHeader(name = "secure", value = "true")})
    public List<User> getAllUsers();

    //没有参数，容器Set返回值
    @HttpMethod(value = "getAllUsers",headers = {@HttpHeader(name = "secure", value = "true")})
    public Set<User> getSetUsers();

    //没有参数，Map返回值
    @HttpMethod(value = "getMapUser",headers = {@HttpHeader(name = "secure", value = "true")})
    public Map<String,User> getMapUser();

    //获得响应json
    @HttpMethod(value = "getAllUsers",headers = {@HttpHeader(name = "secure", value = "true")})
    public String getJson();

    //数组类型
    @HttpMethod(value = "getAllUsers",headers = {@HttpHeader(name = "secure", value = "true")})
    public User[] getUserArray();

}
