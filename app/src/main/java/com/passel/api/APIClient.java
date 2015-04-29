package com.passel.api;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.passel.BuildConfig;
import com.passel.api.messaging.NewEventMessage;
import com.passel.api.messaging.Message;
import com.passel.api.messaging.SignupMessage;
import com.passel.data.JsonMapper;
import com.passel.data.PasselApplication;
import com.passel.util.Err;
import com.passel.util.Ok;
import com.passel.util.Optional;
import com.passel.util.Result;
import com.passel.util.Some;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

/**
 * Created by aneesh on 4/18/15.
 */
public class APIClient {

    public static final String LOGGING_TAG = "PASSEL_APICLIENT";
    private static final String API_BASE_URL = "http://18.111.124.242:5000";

    private final JsonMapper mapper;

    public APIClient(final JsonMapper mapper) { // Reuse a mapper
        this.mapper = mapper;
    }

    public Result<APIResponse, APIError> signup(final String username, final String publicKey) {
        return makeBlockingRequest(new SignupMessage(username, publicKey));
    }

    public Result<APIResponse, APIError> addEvent(String name, Date start, Date end, String description) {
        return makeBlockingRequest(new NewEventMessage(name, start, end, description));
    }

    public Result<APIResponse,APIError> getEvents() {
        return makeBlockingRequest(new Message() {
            @Override
            @JsonIgnore
            public String getEndpoint() {
                return "/events?attendee_id=" + Integer.toString(1); // TODO: don't hardcode ID
            }
        });
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
            if (BuildConfig.DEBUG && !(1 == messages.length)) {
                Log.e(LOGGING_TAG, "Too many messages given to send");
                throw new IllegalArgumentException();
            }
            Message message = messages[0];
            try {
                String requestBody = mapper.serialize(message);
                Optional<String> json;
                if (requestBody.equals("{}")) {
                    json = Optional.empty();
                    requestBody = "";
                } else {
                    json = new Some(requestBody);
                }
                Log.d(LOGGING_TAG, "Sending request to " + message.getEndpoint() + ":" + requestBody);
                Result<APIResponse, APIError> result = request(message.getEndpoint(), json);
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

        private Result<APIResponse, APIError> request(final String endpoint, final Optional<String> json) {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(API_BASE_URL + endpoint);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept", "application/json");

                if (json.isPresent()) {
                    byte[] data = json.get().getBytes();
                    urlConnection.setRequestProperty("Content-type", "application/json");
                    urlConnection.setDoOutput(true);
                    urlConnection.setFixedLengthStreamingMode(data.length);
                    BufferedOutputStream requestBody = new BufferedOutputStream(urlConnection.getOutputStream());
                    requestBody.write(data);
                    requestBody.flush();
                    requestBody.close();
                }

                InputStream responseBody;
                try {
                    responseBody = urlConnection.getInputStream();
                } catch (IOException e) {
                    responseBody = urlConnection.getErrorStream();
                }
                if (null == responseBody) {
                    return new Err<>(new APIError("Could not get a response stream."));
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
