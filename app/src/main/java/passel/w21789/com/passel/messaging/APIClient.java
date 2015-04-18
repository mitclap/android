package passel.w21789.com.passel.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * Created by aneesh on 4/18/15.
 */
public class APIClient {

    private static final String API_BASE_URL = "http://18.189.28.225:5000";

    public static HttpResponse post(String endpoint, String json) throws IOException {
        HttpPost httpPost = new HttpPost(API_BASE_URL + endpoint);
        httpPost.setEntity(new StringEntity(json));
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        return new DefaultHttpClient().execute(httpPost);
    }

    public static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return mapper;
    }
}
