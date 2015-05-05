package com.passel.api.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
// No need to call super implementation of equals and hashcode; explicitly let Lombok know this is OK
@EqualsAndHashCode(callSuper = false)
public final class SignupMessage implements RequestMessage {

    private final String username;
    private final String publicKey;

    @JsonCreator
    public SignupMessage(@JsonProperty("username") String username, @JsonProperty("pubkey") String publicKey) {
        this.username = username;
        this.publicKey = publicKey;
    }
}
