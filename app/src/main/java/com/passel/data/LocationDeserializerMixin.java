package com.passel.data;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by aneesh on 5/3/15.
 */
public class LocationDeserializerMixin {
    LocationDeserializerMixin(@JsonProperty("latitude") final double latitude,
                              @JsonProperty("longitude") final double longitude) {}
}
