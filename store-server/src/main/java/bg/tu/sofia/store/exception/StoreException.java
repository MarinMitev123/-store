package bg.pgmet.mitev.store.exception;

public class MitevStoreException extends RuntimeException {
    private bg.pgmet.mitev.store.exception.ErrorCode errorCode;

    public MitevStoreException(String message) {
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
