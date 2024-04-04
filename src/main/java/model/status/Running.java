package model.status;

import constant.ErrorCode;
import exception.InvalidStatusException;
import exception.KingDeadException;
import java.util.List;
import model.ChessGame;
import model.command.CommandLine;
import model.position.Moving;
import model.position.Position;

public class Running implements GameStatus {

    @Override
    public GameStatus play(final CommandLine commandLine, final ChessGame chessGame) {
        if (commandLine.isEnd()) {
            return new End();
        }
        if (commandLine.isMove()) {
            return status(commandLine, chessGame);
        }
        if (commandLine.isStatus()) {
            return new Running();
        }
        if (commandLine.isQuit()) {
            return new Quit();
        }
        throw new InvalidStatusException(ErrorCode.INVALID_STATUS);
    }

    private GameStatus status(final CommandLine commandLine, final ChessGame chessGame) {
        final Moving moving = convert(commandLine.getBody());
        try {
            chessGame.move(moving);
            return new Running();
        } catch (final KingDeadException exception) {
            return new Checkmate();
        }
    }

    private Moving convert(final List<String> command) {
        final String currentPosition = command.get(CommandLine.CURRENT_POSITION_INDEX);
        final String nextPosition = command.get(CommandLine.NEXT_POSITION_INDEX);

        return new Moving(Position.from(currentPosition), Position.from(nextPosition));
    }

    @Override
    public boolean isRunning() {
        return true;
    }

    @Override
    public boolean isCheck() {
        return false;
    }
}
