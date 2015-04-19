package passel.w21789.com.passel.util;

/**
 * Created by aneesh on 4/18/15.
 */
public class Err<T, E> extends Result<T, E> {
    private E error;
    public Err(E error) {
        this.error = error;
    }

    public E get() {
        return error;
    }

    @Override
    public boolean isOk() {
        return false;
    }

    @Override
    public boolean isErr() {
        return true;
    }
}
