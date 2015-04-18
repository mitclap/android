package passel.w21789.com.passel.messaging;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * Created by aneesh on 4/18/15.
 */
public class APIClient {

    public static HttpResponse post(String uri, String json) throws IOException {
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(new StringEntity(json));
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        return new DefaultHttpClient().execute(httpPost);
    }
}
