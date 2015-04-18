package passel.w21789.com.passel.messaging;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by aneesh on 4/18/15.
 */
public class APIClient {

    private static final String API_BASE_URL = "http://18.189.28.225:5000";

    public HttpResponse signup(SignupMessage message) {
        return makeBlockingRequest(message);
    }

    public HttpResponse addEvent(EventMessage message) {
        return makeBlockingRequest(message);
    }

    private HttpResponse makeBlockingRequest(Message message) {
        // TODO: don't swallow exception but make the caller handle!
        // TODO: use custom exception type with parent pointer instead of exposing raw exceptions though
        // TODO: a la Rust's From<blah>
        try {
            APIRequestTask task = new APIRequestTask();
            task.execute(message);
            return task.get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e("DEBUGGG", "had a problem posting " + message.toString(), e);
            return null;
        }
    }

    private static String serialize(final Message message) throws JsonProcessingException {
        return getObjectMapper().writeValueAsString(message);
    }

    private static HttpResponse post(String endpoint, String json) throws IOException {
        HttpPost httpPost = new HttpPost(API_BASE_URL + endpoint);
        httpPost.setEntity(new StringEntity(json));
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        return new DefaultHttpClient().execute(httpPost);
    }

    private static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return mapper;
    }

    private class APIRequestTask extends AsyncTask<Message, Void, HttpResponse> {
        @Override
        protected HttpResponse doInBackground(Message... messages) {
            assert (1 == messages.length);
            Message message = messages[0];
            try {
                String json = APIClient.serialize(message);
                Log.e("DEBUGGG", "Made a json :)");
                Log.e("DEBUGGG", json);
                HttpResponse response = APIClient.post(message.getEndpoint(), json);
                Log.e("DEBUGGG", "Request was successful :)");
                Log.e("DEBUGGG", response.getEntity().toString());
                return response;
            } catch (JsonProcessingException e) {
                Log.e("DEBUGGG", "Couldn't serialize the json :(");
            } catch (IOException e) {
                Log.e("DEBUGGG", "Error when trying to make the POST request :(", e);
            } catch (RuntimeException e) {
                Log.e("DEBUGGG", "I just failed completely :(", e);
            }
            return null;
        }
    }
}
