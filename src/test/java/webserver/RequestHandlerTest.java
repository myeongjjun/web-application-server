package webserver;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestHandlerTest {

    @Test
    public void mapGetTest() {
        Map<String, String> emptyMap = Collections.emptyMap();
        String out = emptyMap.get("A");
        System.out.println("out = " + out);
    }

}