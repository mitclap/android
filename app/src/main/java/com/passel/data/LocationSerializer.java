package com.passel.data;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Created by aneesh on 5/3/15.
 */
public class LocationSerializer extends JsonSerializer<Location> {
    @Override
    public void serialize(Location location, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeNumberField("latitude", location.getLatitude());
        jgen.writeNumberField("longitude", location.getLongitude());
        jgen.writeEndObject();
    }

    @Override
    public Class<Location> handledType() {
        return Location.class;
    }
}
