package demo.spring.demo;

import com.spring.demo.spi.SpiSpringApplication;
import com.spring.demo.spi.entity.User;
import com.spring.demo.spi.http.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@SpringBootTest(classes = SpiSpringApplication.class)
class DemoApplicationTests {

    @Autowired
    private UserService userService;

    @Test
    public void test(){
        log.info(">>> call start---");
        userService.call();
        log.info(">>> call end---");

        log.info(">>> addUser start---");
        User user = new User("uuu",30);
        userService.addUser(user);
        log.info(">>> addUser end---");

        log.info(">>> getAllUsers start---");
        List<User> list = userService.getAllUsers();
        list.forEach(u ->{
            log.info(u.toString());
        });
        log.info(">>> getAllUsers end---");

        log.info(">>> getUser start---");
        User user1 = userService.getUser("uuu");
        log.info(user1.toString());
        log.info(">>> getUser end---");

        log.info(">>> getSetUsers start---");
        Set<User> set = userService.getSetUsers();
        log.info(set.toString());
        log.info(">>> getSetUsers end---");


        log.info(">>> getMapUser start---");
        Map<String, User> mapUser = userService.getMapUser();
        mapUser.forEach((k,v) ->{
            log.info("k="+k+",v"+v);
        });
        log.info(">>> getMapUser end---");

        log.info(">>> getJson start---");
        String rawJson = userService.getJson();
        log.info(rawJson);
        log.info(">>> getJson end---");

        log.info(">>> getUserArray start---");
        User[] users = userService.getUserArray();
        for (int i = 0; i <users.length ; i++) {
            log.info(users[i].toString());
        }
        log.info(">>> getUserArray end---");


    }

}
