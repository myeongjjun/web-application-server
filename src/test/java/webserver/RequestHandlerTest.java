package webserver;

import db.DataBase;
import model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class RequestHandlerTest {

    @Before
    public void init() {
        DataBase.addUser(new User("test1", "1234", "User1", "test1@test.com"));
        DataBase.addUser(new User("test2", "1234", "User2", "test2@test.com"));
    }

    @Test
    public void mapGetTest() {
        Map<String, String> emptyMap = Collections.emptyMap();
        String out = emptyMap.get("A");
        System.out.println("out = " + out);
    }

    @Test
    public void findAllTest() {
        Collection<User> all = DataBase.findAll();

        Assert.assertEquals(2, all.size());
    }

    @Test
    public void loginSuccessTest() {
        String userId = "test1";
        String password = "1234";

        User findUser = DataBase.findUserById(userId);

        Assert.assertTrue(findUser.validate(userId, password));
    }

    @Test
    public void loginfailedTest() {
        String userId = "test1";
        String password = "12355";

        User findUser = DataBase.findUserById(userId);

        Assert.assertFalse(findUser.validate(userId, password));
    }


}