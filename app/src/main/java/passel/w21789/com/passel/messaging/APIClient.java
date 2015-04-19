package passel.w21789.com.passel.messaging;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
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
                String requestBody = mapper.writeValueAsString(message);
                Log.d(LOGGING_TAG, "Sending request to " + message.getEndpoint() + ":" + requestBody);
                Result<APIResponse, APIError> result = post(message.getEndpoint(), requestBody);
                if (result.isOk()) {
                    APIResponse response = result.unwrap();
                    Log.d(LOGGING_TAG, "[" + Integer.toString(response.getCode()) + "] " + response.getBody());
                } else {
                    APIError error = ((Err<APIResponse, APIError>) result).get();
                    Log.e(LOGGING_TAG, "Got an error", error);
                }
                return result;
            } catch (JsonProcessingException e) {
                Log.e(LOGGING_TAG, "Unable to serialize message:" + message.toString(), e);
            }
            return null;
        }

        private Result<APIResponse, APIError> post(final String endpoint, final String json) {
            byte[] data = json.getBytes();
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(API_BASE_URL + endpoint);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Content-type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");

                urlConnection.setDoOutput(true);
                urlConnection.setFixedLengthStreamingMode(data.length);
                BufferedOutputStream requestBody = new BufferedOutputStream(urlConnection.getOutputStream());
                requestBody.write(data);
                requestBody.flush();
                requestBody.close();

                InputStream responseBody;
                try {
                    responseBody = urlConnection.getInputStream();
                } catch (IOException e) {
                    responseBody = urlConnection.getErrorStream();
                }
                Scanner s = new Scanner(responseBody).useDelimiter("\\A");
                return new Ok<>(new APIResponse(urlConnection.getResponseCode(), s.hasNext() ? s.next() : ""));
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
