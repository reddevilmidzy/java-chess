package controller;

import dto.ChessBoardDto;
import dto.ScoreDto;
import exception.CustomException;
import java.util.List;
import model.ChessGame;
import model.command.CommandLine;
import model.status.GameStatus;
import model.status.StatusFactory;
import view.InputView;
import view.OutputView;

public class ChessController {

    private final InputView inputView;
    private final OutputView outputView;

    public ChessController(final InputView inputView, final OutputView outputView) {
        this.inputView = inputView;
        this.outputView = outputView;
    }

    public void run() {
        final ChessGame chessGame = ChessGame.setupStartingPosition();
        outputView.printStartMessage();
        GameStatus gameStatus = initGame();
        outputView.printChessBoard(ChessBoardDto.from(chessGame));

        while (gameStatus.isRunning()) {
            gameStatus = play(gameStatus, chessGame);
        }
    }

    private GameStatus initGame() {
        try {
            return StatusFactory.create(readCommandLine());
        } catch (final CustomException exception) {
            outputView.printException(exception.getErrorCode());
            return initGame();
        }
    }

    private GameStatus play(final GameStatus gameStatus, final ChessGame chessGame) {
        try {
            final CommandLine commandLine = readCommandLine();
            final GameStatus tmp = gameStatus.play(commandLine, chessGame); // TODO tmp 말고 딴거
            print(commandLine, chessGame);
            return tmp;
        } catch (final CustomException exception) {
            outputView.printException(exception.getErrorCode());
            return play(gameStatus, chessGame);
        }
    }

    //TODO 뭔가 이녀석도 옮겨주기
    private void print(final CommandLine commandLine, final ChessGame chessGame) {
        if (commandLine.isStatus()) {
            outputView.printScore(ScoreDto.from(chessGame));
        }
        if (commandLine.isStart() || commandLine.isMove()) {
            outputView.printChessBoard(ChessBoardDto.from(chessGame));
        }
    }

    private CommandLine readCommandLine() {
        try {
            final List<String> command = inputView.readCommandList();
            return CommandLine.from(command);
        } catch (final CustomException exception) {
            outputView.printException(exception.getErrorCode());
        }
        return readCommandLine();
    }
}
