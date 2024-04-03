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

public class ChessController { // TODO 어떤건 service.메서드, 어떤건 메서드.(service) 이딴식임

    private final InputView inputView;
    private final OutputView outputView;

    public ChessController(final InputView inputView, final OutputView outputView) {
        this.inputView = inputView;
        this.outputView = outputView;
    }

    public void run(final ChessService chessService) {
        outputView.printStartMessage();
        GameStatus gameStatus = initGame();
        final ChessGame chessGame = chessService.bringGame();
        if (gameStatus.isRunning()) {
            outputView.printChessBoard(ChessBoardDto.from(chessGame));
        }
        while (gameStatus.isRunning()) {
            gameStatus = play(chessService, gameStatus, chessGame);
        }
        chessService.save(gameStatus, chessGame);
    }

    private GameStatus initGame() {
        try {
            return StatusFactory.create(readCommandLine());
        } catch (final CustomException exception) {
            outputView.printException(exception.getErrorCode());
            return initGame();
        }
    }

    private GameStatus play(final ChessService chessService, final GameStatus preStatus, final ChessGame chessGame) {
        try {
            final CommandLine commandLine = readCommandLine();
            final GameStatus status = preStatus.play(commandLine, chessGame);
            chessService.saveMoving(chessGame, commandLine);
            print(status, commandLine, chessGame);
            return status;
        } catch (final CustomException exception) {
            outputView.printException(exception.getErrorCode());
            return play(chessService, preStatus, chessGame);
        }
    }

    private void print(final GameStatus gameStatus, final CommandLine commandLine, final ChessGame chessGame) {
        if (gameStatus.isCheck()) {
            outputView.printWinner(chessGame.getCamp().toString());
            return;
        }
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
