package db;

import constant.ErrorCode;
import db.connection.DbConnectionUtil;
import db.dto.TurnDto;
import db.exception.DaoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TurnDao {

    private final String database;

    public TurnDao(final String database) {
        this.database = database;
    }

    public void saveTurn(final TurnDto turnDto) {
        final String query = "INSERT INTO turn values(?, ?)";
        try (final Connection connection = DbConnectionUtil.getConnection(database);
             final PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, turnDto.currentCamp());
            preparedStatement.setInt(2, turnDto.count());
            preparedStatement.executeUpdate();
        } catch (final SQLException exception) {
            throw new DaoException(ErrorCode.FAIL_SAVE);
        }
    }

    public TurnDto findTurn() {
        final String query = "SELECT * FROM turn";
        try (final Connection connection = DbConnectionUtil.getConnection(database);
             final PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            final ResultSet resultSet = preparedStatement.executeQuery();
            return convertToTurn(resultSet);
        } catch (SQLException exception) {
            throw new DaoException(ErrorCode.FAIL_FIND);
        }
    }

    private TurnDto convertToTurn(final ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return new TurnDto(resultSet.getString("camp"), resultSet.getInt("count"));
        }
        return new TurnDto("WHITE", 0);
    }

    public void remove() {
        final String query = "TRUNCATE TABLE turn";
        try (final Connection connection = DbConnectionUtil.getConnection(database);
             final PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.executeUpdate();
        } catch (final SQLException exception) {
            throw new DaoException(ErrorCode.FAIL_DELETE);
        }
    }
}
