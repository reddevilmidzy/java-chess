package db;

import constant.ErrorCode;
import db.connection.DbConnectionUtil;
import db.dto.BoardDto;
import db.dto.PieceDto;
import db.dto.PositionDto;
import db.exception.DaoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class BoardDao {

    private final String database;

    public BoardDao(final String database) {
        this.database = database;
    }

    public void saveBoard(final BoardDto board) {
        final Map<PositionDto, PieceDto> pieces = board.pieces();

        for (final Entry<PositionDto, PieceDto> entry : pieces.entrySet()) {
            savePosition(entry.getKey(), entry.getValue());
        }
    }

    private void savePosition(final PositionDto position, final PieceDto piece) {
        final String query = "INSERT INTO board VALUES(?, ?, ?)";
        try (final Connection connection = DbConnectionUtil.getConnection(database);
             final PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, position.value());
            preparedStatement.setString(2, piece.type());
            preparedStatement.setString(3, piece.camp());
            preparedStatement.executeUpdate();
        } catch (final SQLException exception) {
            throw new DaoException(ErrorCode.FAIL_SAVE);
        }
    }

    public BoardDto find() {
        final String query = "SELECT * FROM board";
        try (final Connection connection = DbConnectionUtil.getConnection(database);
             final PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            final ResultSet resultSet = preparedStatement.executeQuery();
            return convert(resultSet);
        } catch (final SQLException exception) {
            throw new DaoException(ErrorCode.FAIL_FIND);
        }
    }

    private BoardDto convert(final ResultSet resultSet) throws SQLException {
        final Map<PositionDto, PieceDto> result = new HashMap<>();
        while (resultSet.next()) {
            final PieceDto pieceDto = convertToPiece(resultSet);
            final PositionDto positionDto = convertToPosition(resultSet);
            result.put(positionDto, pieceDto);
        }
        if (result.isEmpty()) {
            return BoardDto.create();
        }
        return new BoardDto(result);
    }

    private PieceDto convertToPiece(final ResultSet resultSet) throws SQLException {
        final String type = resultSet.getString("piece_type");
        final String camp = resultSet.getString("camp");
        return new PieceDto(type, camp);
    }

    private PositionDto convertToPosition(final ResultSet resultSet) throws SQLException {
        final String position = resultSet.getString("position");
        return new PositionDto(position);
    }

    public void remove() {
        final String query = "TRUNCATE TABLE board";
        try (final Connection connection = DbConnectionUtil.getConnection(database);
             final PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.executeUpdate();
        } catch (final SQLException exception) {
            throw new DaoException(ErrorCode.FAIL_DELETE);
        }
    }
}
