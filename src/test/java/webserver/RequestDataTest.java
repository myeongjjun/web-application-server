package webserver;

import org.junit.Test;

import java.util.HashMap;
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


    @Test
    public void requestCookiesTest() {
        String url = "/user/create?test=aaa&name=myeong";
        String host = "localhost:8080";
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", "logined=true; JSESSIONID=3DE68EAA1B6DEFDD8432EEFFD48FF89D; Idea-8ddbda83=ba4d6961-874d-4f2f-aa03-f43b6fad23d5");
        RequestData req = RequestData.requestGet(url, host);
        req.addCookie(headers);
        Map<String, String> params = req.getParams();
        Map<String, String> cookies = req.getCookies();

        assertThat(req.getPath(), is("/user/create"));
        assertThat(params.get("test"), is("aaa"));
        assertThat(params.get("name"), is("myeong"));
        assertThat(cookies.get("logined"), is("true"));
    }

    @Test
    public void stringBuilderTest() {
        StringBuilder sb = new StringBuilder("");
        sb.append("aaa");
        System.out.println(sb.toString());
    }
}