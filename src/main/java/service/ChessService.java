package service;

import db.BoardDao;
import db.MovingDao;
import db.TurnDao;
import db.dto.BoardDto;
import db.dto.MovingDto;
import db.dto.TurnDto;
import java.util.List;
import model.Board;
import model.Camp;
import model.ChessGame;
import model.GameTurn;
import model.Turn;
import model.command.CommandLine;
import model.position.Moving;
import model.position.Position;
import model.status.GameStatus;

public class ChessService {

    private final MovingDao movingDao;
    private final TurnDao turnDao;
    private final BoardDao boardDao;

    public ChessService(final String database) {
        this.movingDao = new MovingDao(database);
        this.turnDao = new TurnDao(database);
        this.boardDao = new BoardDao(database);
    }

    public ChessGame bringGame() {
        if (hasGame()) {
            return findGame();
        }
        return ChessGame.setupStartingPosition();
    }

    public void removeAll() {
        turnDao.remove();
        boardDao.remove();
        movingDao.remove();
    }

    public boolean hasGame() {
        return movingDao.countMoving() > 0;
    }

    public void save(final GameStatus gameStatus, final ChessGame chessGame) {
        if (gameStatus.isCheck() || gameStatus.isQuit()) {
            removeAll();
            return;
        }
        final Board board = chessGame.getBoard();
        final Camp camp = chessGame.getCamp();
        final Turn turn = chessGame.getTurn();
        final BoardDto boardDto = BoardDto.from(board);
        final TurnDto turnDto = TurnDto.from(camp, turn);
        save(boardDto, turnDto);
    }

    public void saveMoving(final ChessGame chessGame, final CommandLine commandLine) {
        if (commandLine.isMove()) {
            final List<String> body = commandLine.getBody();
            final Camp camp = chessGame.getCamp().toggle();
            final MovingDto movingDto = MovingDto.from(body, camp);
            saveMoving(movingDto);
        }
    }


    protected void save(final BoardDto board, final TurnDto turnDto) {
        boardDao.remove();
        boardDao.saveBoard(board);
        turnDao.remove();
        turnDao.saveTurn(turnDto);
    }

    protected void saveMoving(final MovingDto moving) {
        movingDao.addMoving(moving);
    }

    public ChessGame findGame() {
        final BoardDto findBoard = findBoard();
        final TurnDto findTurn = findTurn();
        final List<MovingDto> findMoving = movingDao.findAll();
        final Board board = findBoard.toBoard();
        if (unsaved(findTurn, findMoving)) {
            restore(findTurn, findMoving, board);
        }
        final Camp camp = Camp.calculateTurn(findMoving.size());
        final GameTurn gameTurn = new GameTurn(camp, new Turn(findMoving.size()));
        return new ChessGame(board, gameTurn);
    }

    private BoardDto findBoard() {
        return boardDao.find();
    }

    private TurnDto findTurn() {
        return turnDao.findTurn();
    }

    private boolean unsaved(final TurnDto findTurn, final List<MovingDto> findMoving) {
        final int turnCount = findTurn.count();
        final int movingCount = findMoving.size();
        return turnCount < movingCount;
    }

    private void restore(final TurnDto findTurn, final List<MovingDto> findMoving, final Board board) {
        for (int i = findTurn.count(); i < findMoving.size(); i++) {
            final MovingDto movingDto = findMoving.get(i);
            final Moving moving = new Moving(Position.from(movingDto.current()), Position.from(movingDto.next()));
            board.move(moving);
        }
    }
}
