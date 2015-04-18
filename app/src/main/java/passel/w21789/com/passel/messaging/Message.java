package passel.w21789.com.passel.messaging;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by aneesh on 4/18/15.
 */
public interface Message {
    public abstract String getEndpoint();
}
