package db.exception;

import constant.ErrorCode;

public class DaoException extends DbException {

    public DaoException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
