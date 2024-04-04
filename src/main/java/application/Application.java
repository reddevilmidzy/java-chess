package application;

import controller.ChessController;
import db.exception.DbException;
import service.ChessService;
import view.OutputView;

public class Application {

    public static void main(final String[] args) {
        final ChessService chessService = new ChessService("chess");
        final ChessController chessController = new ChessController(chessService);

        try {
            chessController.run();
        } catch (final DbException exception) {
            OutputView.printException(exception.getErrorCode());
        }
    }
}
