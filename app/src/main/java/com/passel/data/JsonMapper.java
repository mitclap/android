package com.passel.data;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by aneesh on 4/20/15.
 * <p/>
 * Wrap Jackson to ensure consistent mapper usage.
 * TODO: fully wrap error and typeref types for interoperability with i.e. gson
 */
public class JsonMapper {
    private ObjectMapper mapper = new ObjectMapper();

    public JsonMapper() {
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        SimpleModule locationSerializer = new SimpleModule();
        locationSerializer.addSerializer(new LocationSerializer());
        mapper.registerModule(locationSerializer);

        mapper.addMixInAnnotations(Location.class, LocationDeserializerMixin.class);
    }

    public String serialize(final Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    public <T> T deserialize(final String json, final TypeReference<T> type)
            throws JsonParseException, JsonMappingException {
        try {
            return mapper.readValue(json, type);
        } catch (JsonParseException | JsonMappingException e) {
            throw e;
        } catch (IOException e) {
            Log.e("PASSEL_JsonMapper", "IOException when deserializing a string", e);
            throw new RuntimeException();
        }
    }
}
