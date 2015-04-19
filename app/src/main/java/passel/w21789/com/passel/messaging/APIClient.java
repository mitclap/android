package passel.w21789.com.passel.messaging;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * Created by aneesh on 4/18/15.
 */
public class APIClient {

    public static final String LOGGING_TAG = "PASSEL_APICLIENT";
    private static final String API_BASE_URL = "http://18.189.28.225:5000";

    private ObjectMapper mapper = new ObjectMapper();

    public APIClient() {
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
    }

    public Result<APIResponse, APIError> signup(final String username, final String publicKey) {
        return makeBlockingRequest(new SignupMessage(username, publicKey));
    }

    public Result<APIResponse, APIError> addEvent(String name, Date start, Date end, String description) {
        return makeBlockingRequest(new EventMessage(name, start, end, description));
    }

    private Result<APIResponse, APIError> makeBlockingRequest(final Message message) {
        try {
            APIRequestTask task = new APIRequestTask();
            task.execute(message);
            return task.get();
        } catch (ExecutionException | InterruptedException e) {
            return new Err<>(new APIError(e));
        }
    }

    private class APIRequestTask extends AsyncTask<Message, Void, Result<APIResponse, APIError>> {
        @Override
        protected Result<APIResponse, APIError> doInBackground(Message... messages) {
            assert (1 == messages.length);
            Message message = messages[0];
            try {
                String json = mapper.writeValueAsString(message);
                Log.d(LOGGING_TAG, json);
                Result<APIResponse, APIError> result = post(message.getEndpoint(), json);
                Log.e(LOGGING_TAG, result.toString());
                if (result.isOk()) {
                    Log.e(LOGGING_TAG, "Request was successful :)");
                    APIResponse response = result.unwrap();
                    Log.e(LOGGING_TAG, Integer.toString(response.getCode()));
                    Log.e(LOGGING_TAG, response.getResponse());
                } else {
                    APIError error = ((Err<APIResponse, APIError>) result).get();
                    Log.e(LOGGING_TAG, "got an error", error);
                }
                return result;
            } catch (JsonProcessingException e) {
                Log.e(LOGGING_TAG, "Couldn't serialize the json :(");
            } catch (IOException e) {
                Log.e(LOGGING_TAG, "Error when trying to make the POST request :(", e);
            } catch (RuntimeException e) {
                Log.e(LOGGING_TAG, "I just failed completely :(", e);
            }
            return null;
        }

        private Result<APIResponse, APIError> post(final String endpoint, final String json) {
            byte[] data = json.getBytes();
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(API_BASE_URL + endpoint);
                Log.e(LOGGING_TAG, url.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Content-type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");

                urlConnection.setDoOutput(true);
                urlConnection.setFixedLengthStreamingMode(data.length);
                BufferedOutputStream body = new BufferedOutputStream(urlConnection.getOutputStream());
                body.write(data);
                body.flush();
                body.close();

                return new Ok<>(new APIResponse(urlConnection.getResponseCode(), urlConnection.getResponseMessage()));
            } catch (IOException e) {
                return new Err<>(new APIError(e));
            } finally {
                if (null != urlConnection) {
                    urlConnection.disconnect();
                }
            }
        }
    }
}
