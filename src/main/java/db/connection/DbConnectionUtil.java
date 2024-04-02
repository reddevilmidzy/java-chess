package db.connection;

import constant.ErrorCode;
import db.exception.ConnectionException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnectionUtil {

    private static final String SERVER = "localhost:13306";
    private static final String OPTION = "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    private DbConnectionUtil() {
    }

    public static Connection getConnection(final String database) {
        try {
            final String url = "jdbc:mysql://" + SERVER + "/" + database + OPTION;
            return DriverManager.getConnection(url, USERNAME, PASSWORD);
        } catch (final SQLException exception) {
            throw new ConnectionException(ErrorCode.CONNECTION);
        }
    }
}
