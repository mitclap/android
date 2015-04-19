package passel.w21789.com.passel.api;

/**
 * Created by aneesh on 4/18/15.
 */
public class APIError extends Exception {
    public APIError(Throwable throwable) {
        super(throwable);
    }
}
