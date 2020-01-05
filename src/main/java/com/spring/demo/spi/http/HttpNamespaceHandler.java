package com.spring.demo.spi.http;


import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class HttpNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser("http", new HttpBeanDefinitionParser());
    }
}
