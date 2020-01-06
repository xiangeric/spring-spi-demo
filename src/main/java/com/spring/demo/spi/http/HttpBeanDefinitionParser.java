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
        //新建RootBeanDefinition
        RootBeanDefinition bd = new RootBeanDefinition();
        if(!StringUtils.isEmpty(targetClass)){
            try {
                //加载对应的Class
                Class<?> clazz = Class.forName(targetClass);
                //校验Class
                String validResult = validateHttpMethods(clazz);
                //如果存在异常信息，则抛出异常
                if(validResult!=null){
                    throw new Exception("class ["+ targetClass+"] should not has condition:"+validResult);
                }
                //调用CGLIB生成代理
                Enhancer enhancer = new Enhancer();
                enhancer.setInterfaces(new Class[]{clazz});
                enhancer.setSuperclass(Object.class);
                //解析地址，兼容${}方式设置地址
                address = parserContext.getReaderContext().getEnvironment()
                        .resolveRequiredPlaceholders(address);
                //设置自定义方法调用拦截器，在调用每一个方法时，将被该拦截器拦截
                enhancer.setCallback(new HttpMethodInterceptor(address));
                //如果id为空，则自动生成beanName
                if(StringUtils.isEmpty(beanName)){
                    beanName = beanNameGenerator.generateBeanName(bd,parserContext.getRegistry());
                }
                //当生成实例时，如果BeanFactory监测到BeanDefinition存在Supplier，则使用Supplier创建实例，这里生成实例的动作
                //委托给CGLIB
                bd.setInstanceSupplier(enhancer::create);
                //设置BeanClass
                bd.setBeanClass(clazz);
                //将BeanDefinition注册到BeanRegistry中
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


    /**
     * 校验是否满足条件
     * (1) 方法都应该被@HttpMethod修饰
     * (2) 方法至多只能存在一个参数
     **/
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
