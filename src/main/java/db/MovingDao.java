package db;

import constant.ErrorCode;
import db.connection.DbConnectionUtil;
import db.dto.MovingDto;
import db.exception.DaoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MovingDao {

    private final String database;

    public MovingDao(final String database) {
        this.database = database;
    }

    public long addMoving(final MovingDto moving) {
        final String query = "INSERT INTO moving VALUES(?, ?, ?, ?)";
        try (final Connection connection = DbConnectionUtil.getConnection(database);
             final PreparedStatement preparedStatement = connection.prepareStatement(query,
                     Statement.RETURN_GENERATED_KEYS)) {
            preparedStatementSet(moving, preparedStatement);
            preparedStatement.executeUpdate();
            return increaseKey(preparedStatement.getGeneratedKeys());
        } catch (final SQLException exception) {
            throw new DaoException(ErrorCode.FAIL_SAVE);
        }
    }

    private long increaseKey(final ResultSet generatedKeys) throws SQLException {
        if (generatedKeys.next()) {
            return generatedKeys.getLong(1);
        }
        return 0;
    }

    private void preparedStatementSet(final MovingDto moving, final PreparedStatement preparedStatement)
            throws SQLException {
        preparedStatement.setNull(1, 0);
        preparedStatement.setString(2, moving.camp());
        preparedStatement.setString(3, moving.current());
        preparedStatement.setString(4, moving.next());
    }

    public List<MovingDto> findAll() {
        final String query = "SELECT * FROM moving";
        try (final Connection connection = DbConnectionUtil.getConnection(database);
             final PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            final ResultSet resultSet = preparedStatement.executeQuery();
            return convert(resultSet);
        } catch (SQLException exception) {
            throw new DaoException(ErrorCode.FAIL_FIND);
        }
    }

    private List<MovingDto> convert(final ResultSet resultSet) throws SQLException {
        final List<MovingDto> moving = new ArrayList<>();
        while (resultSet.next()) {
            final String camp = resultSet.getString("camp");
            final String current = resultSet.getString("start");
            final String next = resultSet.getString("destination");
            moving.add(new MovingDto(camp, current, next));
        }
        return moving;
    }

    public int countMoving() {
        final String query = "SELECT count(*) AS count FROM moving";
        try (final Connection connection = DbConnectionUtil.getConnection(database);
             final PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            final ResultSet resultSet = preparedStatement.executeQuery();
            return convertToCount(resultSet);
        } catch (final SQLException exception) {
            throw new DaoException(ErrorCode.FAIL_FIND);
        }
    }

    private int convertToCount(final ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return resultSet.getInt("count");
        }
        throw new DaoException(ErrorCode.FAIL_FIND);
    }

    public MovingDto findByMovementId(final long movementId) {
        final String query = "SELECT * FROM moving WHERE movement_id = ?";
        try (final Connection connection = DbConnectionUtil.getConnection(database);
             final PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, movementId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            return convertToMoving(resultSet);
        } catch (final SQLException exception) {
            throw new DaoException(ErrorCode.FAIL_FIND);
        }
    }

    private MovingDto convertToMoving(final ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return new MovingDto(
                    resultSet.getString("camp"),
                    resultSet.getString("start"),
                    resultSet.getString("destination")
            );
        }
        throw new DaoException(ErrorCode.FAIL_FIND);
    }

    public void remove() {
        final String query = "TRUNCATE TABLE moving";
        try (final Connection connection = DbConnectionUtil.getConnection(database);
             final PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.executeUpdate();
        } catch (final SQLException exception) {
            throw new DaoException(ErrorCode.FAIL_DELETE);
        }
    }
}
