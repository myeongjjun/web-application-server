package webserver;

import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RequestDataTest {


    @Test
    public void requestGetTest() {
        String url = "/user/create";
        String host = "localhost:8080";
        String body = "test=aaa&name=myeong";
        RequestData req = RequestData.requestPost(url, host, 0, body);

        Map<String, String> params = req.getParams();
        assertThat(req.getPath(), is(url));
        assertThat(params.get("test"), is("aaa"));
        assertThat(params.get("name"), is("myeong"));
    }


    @Test
    public void requestGetParamsTest() {
        String url = "/user/create?test=aaa&name=myeong";
        String host = "localhost:8080";
        RequestData req = RequestData.requestGet(url, host);

        Map<String, String> params = req.getParams();
        assertThat(req.getPath(), is("/user/create"));
        assertThat(params.get("test"), is("aaa"));
        assertThat(params.get("name"), is("myeong"));
    }
}