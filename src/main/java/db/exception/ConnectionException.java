package db.exception;

import constant.ErrorCode;

public class ConnectionException extends DbException {

    public ConnectionException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
