package passel.w21789.com.passel.messaging;

/**
 * Created by aneesh on 4/18/15.
 */
public abstract class Result<T, E> {
    public abstract boolean isOk();

    public abstract boolean isErr();

    public T unwrap() throws ClassCastException {
        return ((Ok<T, E>) this).get();
    }
}
