package demo.spring.demo;

import com.spring.demo.spi.SpiSpringApplication;
import com.spring.demo.spi.entity.User;
import com.spring.demo.spi.http.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootTest(classes = SpiSpringApplication.class)
class DemoApplicationTests {

    @Autowired
    private UserService userService;


    @Test
    public void test(){
        userService.call();
        User user = new User("uuu",30);
        userService.addUser(user);
        List<User> list = userService.getAllUsers();
        list.forEach(System.out::println);
        User user1 = userService.getUser("uuu");
        System.out.println(user1);
        Set<User> set = userService.getSetUsers();
        Map<String, User> mapUser = userService.getMapUser();
        mapUser.forEach((k,v) ->{
            System.out.println("k="+k+",v"+v);
        });
        String rawJson = userService.getJson();
        System.out.println(rawJson);
        User[] users = userService.getUserArray();
        for (int i = 0; i <users.length ; i++) {
            System.out.println(users[i]);
        }


    }

    public static void main(String[] args) {

    }




    public List<User> list(){
        return null;
    }

}
