package controller;

import service.ChessService;
import db.dto.BoardDto;
import db.dto.MovingDto;
import db.dto.TurnDto;
import dto.ChessBoardDto;
import dto.ScoreDto;
import exception.CustomException;
import java.util.List;
import model.Board;
import model.Camp;
import model.ChessGame;
import model.Turn;
import model.command.CommandLine;
import model.status.GameStatus;
import model.status.StatusFactory;
import view.InputView;
import view.OutputView;

public class ChessController {

    private final InputView inputView;
    private final OutputView outputView;
    private final ChessService chessService = new ChessService("chess");

    public ChessController(final InputView inputView, final OutputView outputView) {
        this.inputView = inputView;
        this.outputView = outputView;
    }

    public void run() {
        outputView.printStartMessage();
        GameStatus gameStatus = initGame();
        final ChessGame chessGame = create();
        if (gameStatus.isRunning()) {
            outputView.printChessBoard(ChessBoardDto.from(chessGame));
        }
        while (gameStatus.isRunning()) {
            gameStatus = play(gameStatus, chessGame);
        }
        save(gameStatus, chessGame);
    }

    private void save(final GameStatus gameStatus, final ChessGame chessGame) {
        if (gameStatus.isCheck() || gameStatus.isQuit()) {
            chessService.removeAll();
            return;
        }
        final Board board = chessGame.getBoard();
        final Camp camp = chessGame.getCamp();
        final Turn turn = chessGame.getTurn();
        final BoardDto boardDto = BoardDto.from(board);
        final TurnDto turnDto = TurnDto.from(camp, turn);
        chessService.save(boardDto, turnDto);
    }

    private ChessGame create() {
        if (chessService.hasGame()) {
            return chessService.findGame();
        }
        return ChessGame.setupStartingPosition();
    }

    private GameStatus initGame() {
        try {
            return StatusFactory.create(readCommandLine());
        } catch (final CustomException exception) {
            outputView.printException(exception.getErrorCode());
            return initGame();
        }
    }

    private GameStatus play(final GameStatus preStatus, final ChessGame chessGame) {
        try {
            final CommandLine commandLine = readCommandLine();
            final GameStatus status = preStatus.play(commandLine, chessGame);
            saveMoving(chessGame, commandLine);
            print(status, commandLine, chessGame);
            return status;
        } catch (final CustomException exception) {
            outputView.printException(exception.getErrorCode());
            return play(preStatus, chessGame);
        }
    }

    private void saveMoving(final ChessGame chessGame, final CommandLine commandLine) {
        if (commandLine.isMove()) {
            final List<String> body = commandLine.getBody();
            final Camp camp = chessGame.getCamp().toggle();
            final MovingDto movingDto = MovingDto.from(body, camp);
            chessService.saveMoving(movingDto);
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
