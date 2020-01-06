package com.spring.demo.spi.http.anno;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited //表明CGLIB子类方法可以继承
public @interface HttpMethod {
    //子地址
    String value();

    HttpHeader[] headers() default {};
}
