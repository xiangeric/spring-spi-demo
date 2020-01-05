package com.spring.demo.spi.http;


import com.spring.demo.spi.http.anno.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import java.lang.reflect.Method;

@Slf4j
public class HttpBeanDefinitionParser implements BeanDefinitionParser {

    private static final DefaultBeanNameGenerator beanNameGenerator = new DefaultBeanNameGenerator();


    private static final String ID_ATTRIBUTE = "id";


    private static final String TARGET_CLASS_ATTRIBUTE =  "targetClass";

    private static final String ADDRESS_ATTRIBUTE =  "address";


    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String beanName = element.getAttribute(ID_ATTRIBUTE);
        String targetClass  = element.getAttribute(TARGET_CLASS_ATTRIBUTE);
        String address = element.getAttribute(ADDRESS_ATTRIBUTE);
        RootBeanDefinition bd = new RootBeanDefinition();
        if(!StringUtils.isEmpty(targetClass)){
            try {
                Class<?> clazz = Class.forName(targetClass);
                String validResult = validateHttpMethods(clazz);
                if(validResult!=null){
                    throw new Exception("class ["+ targetClass+"] should not has condition:"+validResult);
                }
                Enhancer enhancer = new Enhancer();
                enhancer.setInterfaces(new Class[]{clazz});
                enhancer.setSuperclass(Object.class);
                address = parserContext.getReaderContext().getEnvironment()
                        .resolveRequiredPlaceholders(address);
                enhancer.setCallback(new HttpMethodInterceptor(address));
                if(StringUtils.isEmpty(beanName)){
                    beanName = beanNameGenerator.generateBeanName(bd,parserContext.getRegistry());
                }
                bd.setInstanceSupplier(enhancer::create);
                bd.setBeanClass(clazz);
                parserContext.getRegistry().registerBeanDefinition(beanName, bd);
                return bd;
            } catch (ClassNotFoundException e) {
                parserContext.getReaderContext()
                        .error("can not load the class: "+ targetClass,e);
            } catch (Exception e){
                parserContext.getReaderContext()
                        .error("error occur :",e);
            }

        }
        return null;
    }

    private String validateHttpMethods(Class<?> clazz) {
        if(!clazz.isInterface()){
            return "the target class is not interface";
        }
        Method[] methods = clazz.getDeclaredMethods();
        for(Method m:methods){
            HttpMethod ann = m.getDeclaredAnnotation(HttpMethod.class);
            if(ann!=null){
                int count = m.getParameterCount();
                if(count>1){
                    return "@HttpMethod method just only has no parameter or has a parameter";
                }
            }else{
                return "method should be @HttpMethod method";
            }

        }
        return null;
    }
}
