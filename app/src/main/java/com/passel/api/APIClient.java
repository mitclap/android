package com.passel.api;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.passel.BuildConfig;
import com.passel.api.messaging.AccountCreatedMessage;
import com.passel.api.messaging.CheckinCreatedMessage;
import com.passel.api.messaging.CheckinMessage;
import com.passel.api.messaging.EmptyMessage;
import com.passel.api.messaging.EventCreatedMessage;
import com.passel.api.messaging.NewEventMessage;
import com.passel.api.messaging.RequestMessage;
import com.passel.api.messaging.ResponseMessage;
import com.passel.api.messaging.SignupMessage;
import com.passel.data.JsonMapper;
import com.passel.data.Location;
import com.passel.util.Err;
import com.passel.util.Ok;
import com.passel.util.Optional;
import com.passel.util.Result;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import lombok.Value;

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

    public Result<APIResponse, APIError> signup(final String username,
                                                final String publicKey) {
        return makeBlockingRequest(new Request(new SignupMessage(username, publicKey),
                "/accounts",
                mapper.constructType(AccountCreatedMessage.class)));
    }

    public Result<APIResponse, APIError> addEvent(final String name,
                                                  final Date start,
                                                  final Date end,
                                                  final String description) {
        return makeBlockingRequest(new Request(new NewEventMessage(name, start, end, description),
                "/events",
                mapper.constructType(EventCreatedMessage.class)));
    }

    public Result<APIResponse, APIError> addCheckin(final int eventId,
                                                    final Date timestamp,
                                                    final Location location) {
        return makeBlockingRequest(new Request(new CheckinMessage(1, eventId, timestamp, location),
                "/checkins",
                mapper.constructType(CheckinCreatedMessage.class)));
    }

    // TODO these null response types

    public Result<APIResponse,APIError> getEvents() {
        return makeBlockingRequest(new Request(new EmptyMessage(),
                "/events?attendee_id=" + Integer.toString(1),
                mapper.getTypeFactory().constructParametricType(Map.class, String.class, Object.class)));
    }

    private Result<APIResponse, APIError> makeBlockingRequest(final Request request) {
        try {
            APIRequestTask task = new APIRequestTask();
            task.execute(request);
            return task.get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(LOGGING_TAG, "Unable to finish blocking request", e);
            return new Err<>(new APIError(e));
        }
    }

    private class APIRequestTask extends AsyncTask<Request, Void, Result<APIResponse, APIError>> {
        @Override
        protected Result<APIResponse, APIError> doInBackground(Request... requests) {
            if (BuildConfig.DEBUG && !(1 == requests.length)) {
                Log.e(LOGGING_TAG, "Too many messages given to send");
                throw new IllegalArgumentException();
            }
            Request request = requests[0];
            RequestMessage requestMessage = request.getRequestMessage();
            try {
                String requestBody = mapper.serialize(requestMessage);
                Optional<String> json;
                if (requestBody.equals("{}")) {
                    json = Optional.empty();
                    requestBody = "";
                } else {
                    json = Optional.of(requestBody);
                }
                String endpoint = request.getEndpoint();
                Log.d(LOGGING_TAG, "Sending request to " + endpoint + ":" + requestBody);
                Result<APIResponse, APIError> result = request(endpoint, json, request.getResponseType());
                if (!result.isOk()) {
                    APIError error = ((Err<APIResponse, APIError>) result).get();
                    Log.e(LOGGING_TAG, "Got an error", error);
                }
                return result;
            } catch (JsonProcessingException e) {
                Log.e(LOGGING_TAG, "Unable to serialize message:" + requestMessage.toString(), e);
            }
            return null;
        }

        private Result<APIResponse, APIError> request(final String endpoint,
                                                          final Optional<String> requestJson,
                                                          final JavaType responseType) {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(API_BASE_URL + endpoint);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept", "application/json");

                if (requestJson.isPresent()) {
                    byte[] data = requestJson.get().getBytes();
                    urlConnection.setRequestProperty("Content-type", "application/json");
                    urlConnection.setDoOutput(true);
                    urlConnection.setFixedLengthStreamingMode(data.length);
                    BufferedOutputStream requestBody = new BufferedOutputStream(urlConnection.getOutputStream());
                    requestBody.write(data);
                    requestBody.flush();
                    requestBody.close();
                }

                InputStream responseStream;
                try {
                    responseStream = urlConnection.getInputStream();
                } catch (IOException e) {
                    responseStream = urlConnection.getErrorStream();
                }
                if (null == responseStream) {
                    return new Err<>(new APIError("Could not get a response stream."));
                }
                Scanner s = new Scanner(responseStream).useDelimiter("\\A");
                int responseCode = urlConnection.getResponseCode();
                String responseBody = s.hasNext() ? s.next() : "";
                Log.d(LOGGING_TAG, "[" + Integer.toString(responseCode) + "] " + responseBody);
                try {
                    ResponseMessage responseMessage = mapper.deserialize(responseBody,
                            mapper.getTypeFactory().constructParametricType(ResponseMessage.class, responseType));
                    return new Ok<>(new APIResponse(responseCode, responseMessage));
                } catch (JsonParseException | JsonMappingException e) {
                    Log.e(LOGGING_TAG, "Unable to deserialize message:" + requestJson, e);
                    return new Err<>(new APIError(e));
                }
            } catch (IOException e) {
                return new Err<>(new APIError(e));
            } finally {
                if (null != urlConnection) {
                    urlConnection.disconnect();
                }
            }
        }
    }

    @Value
    private class Request {
        RequestMessage requestMessage;
        String endpoint;
        JavaType responseType;

        public Request(final RequestMessage requestMessage,
                       final String endpoint,
                       final JavaType responseType) {
            this.requestMessage = requestMessage;
            this.endpoint = endpoint;
            this.responseType = responseType;
        }
    }
}