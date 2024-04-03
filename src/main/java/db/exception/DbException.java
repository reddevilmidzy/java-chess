package db.exception;

import constant.ErrorCode;

public class DbException extends RuntimeException {

    private final ErrorCode errorCode;

    public DbException(final ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
