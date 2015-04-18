package passel.w21789.com.passel.messaging;

import lombok.EqualsAndHashCode;
import lombok.Value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Value
// No need to call super implementation of equals and hashcode; explicitly let Lombok know this is OK
@EqualsAndHashCode(callSuper = false)
public class SignupMessage {

    private final String username;
    private final String publicKey;

    @JsonCreator
    public SignupMessage(@JsonProperty("username") String username, @JsonProperty("pubkey") String publicKey) {
        this.username = username;
        this.publicKey = publicKey;
    }
}