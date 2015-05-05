package com.passel.api.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

/**
 * Created by aneesh on 5/5/15.
 */
@Value
public class AccountCreatedMessage {
    int accountId;

    public AccountCreatedMessage(@JsonProperty("accountId") int accountId) {
        this.accountId = accountId;
    }
}
