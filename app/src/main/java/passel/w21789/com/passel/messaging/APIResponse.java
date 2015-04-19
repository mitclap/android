package passel.w21789.com.passel.messaging;

import lombok.Value;

/**
 * Created by aneesh on 4/18/15.
 */
@Value
public class APIResponse {
    private int code;
    private String response;

    public APIResponse(int code, String response) {
        this.code = code;
        this.response = response;
    }
}
