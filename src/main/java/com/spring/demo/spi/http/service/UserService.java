package com.spring.demo.spi.http.service;

import com.spring.demo.spi.entity.User;
import com.spring.demo.spi.http.anno.HttpHeader;
import com.spring.demo.spi.http.anno.HttpMethod;

import java.util.List;

public interface UserService {


    @HttpMethod(value = "call",headers = {@HttpHeader(name = "secure", value = "true")})
    public void call();

    @HttpMethod(value = "addUser",headers = {@HttpHeader(name = "secure", value = "true")})
    public void addUser(User user);


    @HttpMethod(value = "getUser",headers = {@HttpHeader(name = "secure", value = "true")})
    public User getUser(String name);

    @HttpMethod(value = "getAllUsers",headers = {@HttpHeader(name = "secure", value = "true")})
    public List<User> getAllUsers();

}
