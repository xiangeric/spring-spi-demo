package com.spring.demo.spi.controller;

import com.spring.demo.spi.entity.User;
import com.spring.demo.spi.http.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class UserViewController {

    @Autowired
    private UserService userService;


    @RequestMapping("/call")
    public void call(){
        userService.call();
    }

    @RequestMapping("/add")
    public void addUser(){
        User user = new User();
        user.setAge(26);
        user.setName("chen eric");
        userService.addUser(user);
    }

    @RequestMapping("/get")
    public void get(){
        User user = userService.getUser("chen eric");
        System.out.println(user);
    }

    @RequestMapping("/getAllUsers")
    public void getAllUsers(){
        List<User> list = userService.getAllUsers();
        if(list!=null && !list.isEmpty()){
            list.forEach(System.out::println);
        }
    }


}
