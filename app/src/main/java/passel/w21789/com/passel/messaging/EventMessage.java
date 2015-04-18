package passel.w21789.com.passel.messaging;

import lombok.EqualsAndHashCode;
import lombok.Value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Value
// No need to call super implementation of equals and hashcode; explicitly let Lombok know this is OK
@EqualsAndHashCode(callSuper = false)
public class EventMessage {

    private final String eventName;
    private final String startDateTime;
    private final String stopDateTime;
    private final String description;

    @JsonCreator
    public EventMessage(@JsonProperty("eventName") String eventName,
                        @JsonProperty("startDateTime") String startDateTime,
                        @JsonProperty("stopDateTime") String stopDateTime
                        ,@JsonProperty("stopDateTime") String description) {
        this.eventName = eventName;
        this.startDateTime = startDateTime;
        this.stopDateTime = stopDateTime;
        this.description= description;
    }
}
