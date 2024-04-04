package application;

import controller.ChessController;
import db.exception.DbException;
import java.util.Scanner;
import service.ChessService;
import view.InputView;
import view.OutputView;

public class Application {

    public static void main(final String[] args) {
        final InputView inputView = new InputView(new Scanner(System.in));
        final OutputView outputView = new OutputView();
        final ChessService chessService = new ChessService("chess");

        final ChessController chessController = new ChessController(inputView, outputView, chessService);
        try {
            chessController.run();
        } catch (final DbException exception) {
            outputView.printException(exception.getErrorCode());
        }
    }
}
