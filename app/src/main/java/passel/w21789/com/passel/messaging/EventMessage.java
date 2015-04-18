package passel.w21789.com.passel.messaging;

import lombok.EqualsAndHashCode;
import lombok.Value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@Value
// No need to call super implementation of equals and hashcode; explicitly let Lombok know this is OK
@EqualsAndHashCode(callSuper = false)
public class EventMessage implements Message {

    private final String name;
    private final Date start;
    private final Date end;
    private final String description;

    @JsonCreator
    public EventMessage(@JsonProperty("name") String name,
                        @JsonProperty("start") Date start,
                        @JsonProperty("end") Date end
                        ,@JsonProperty("description") String description) {
        this.name = name;
        this.start = start;
        this.end = end;
        this.description= description;
    }

    @Override
    public String getEndpoint() {
        return "/events";
    }
}
