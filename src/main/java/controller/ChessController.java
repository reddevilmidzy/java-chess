package controller;

import dto.ChessBoardDto;
import dto.ScoreDto;
import exception.CustomException;
import java.util.List;
import model.ChessGame;
import model.command.CommandLine;
import model.status.GameStatus;
import model.status.StatusFactory;
import service.ChessService;
import view.InputView;
import view.OutputView;

public class ChessController {

    private final ChessService chessService;

    public ChessController(final ChessService chessService) {
        this.chessService = chessService;
    }

    public void run() {
        OutputView.printStartMessage();
        GameStatus gameStatus = initGame();
        final ChessGame chessGame = chessService.bringGame();
        if (gameStatus.isRunning()) {
            OutputView.printChessBoard(ChessBoardDto.from(chessGame));
        }
        while (gameStatus.isRunning()) {
            gameStatus = play(gameStatus, chessGame);
        }
        chessService.save(gameStatus, chessGame);
    }

    private GameStatus initGame() {
        try {
            return StatusFactory.create(readCommandLine());
        } catch (final CustomException exception) {
            OutputView.printException(exception.getErrorCode());
            return initGame();
        }
    }

    private GameStatus play(final GameStatus preStatus, final ChessGame chessGame) {
        try {
            final CommandLine commandLine = readCommandLine();
            final GameStatus status = preStatus.play(commandLine, chessGame);
            chessService.saveMoving(chessGame, commandLine);
            print(status, commandLine, chessGame);
            return status;
        } catch (final CustomException exception) {
            OutputView.printException(exception.getErrorCode());
            return play(preStatus, chessGame);
        }
    }

    private void print(final GameStatus gameStatus, final CommandLine commandLine, final ChessGame chessGame) {
        if (gameStatus.isCheck()) {
            OutputView.printWinner(chessGame.getCamp().toString());
            return;
        }
        if (commandLine.isStatus()) {
            OutputView.printScore(ScoreDto.from(chessGame));
        }
        if (commandLine.isStart() || commandLine.isMove()) {
            OutputView.printChessBoard(ChessBoardDto.from(chessGame));
        }
    }

    private CommandLine readCommandLine() {
        try {
            final List<String> command = InputView.readCommandList();
            return CommandLine.from(command);
        } catch (final CustomException exception) {
            OutputView.printException(exception.getErrorCode());
        }
        return readCommandLine();
    }
}
