package demo.spring.demo;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.demo.spi.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

class DemoApplicationTests {

    public static void main(String[] args) throws Exception{
//        String jsonString = "[{\"name\":\"chen eric\",\"age\":26},{\"name\":\"liao e\",\"age\":26}]";
//        ObjectMapper objectMapper = new ObjectMapper();
//        Method method = DemoApplicationTests.class.getDeclaredMethod("list",null);
////        System.out.println(method.getGenericReturnType());
////        ParameterizedType type = (ParameterizedType) method.getGenericReturnType();
////        Class c = (Class)type.getRawType();
////        System.out.println(c+"---");
////        Class genericClazz = (Class)type.getActualTypeArguments()[0];
////        System.out.println(genericClazz);
////        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(c, genericClazz);
//        List<User> list = objectMapper.readValue(jsonString, getJavaType(objectMapper,method.getReturnType(),method.getGenericReturnType()));

    }




    public List<User> list(){
        return null;
    }

}
