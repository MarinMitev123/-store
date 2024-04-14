package bg.tu.sofia.store.exception;

public class StoreException extends RuntimeException {
    private ErrorCode errorCode;

    public StoreException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        if (errorCode == null) {
            return super.getMessage();
        }
        return errorCode.getErrorMessage();
    }
}
