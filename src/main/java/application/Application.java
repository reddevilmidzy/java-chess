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
        final ChessController chessController = new ChessController(inputView, outputView);
        final ChessService chessService = new ChessService("chess");

        try {
            chessController.run(chessService);
        } catch (final DbException exception) {
            outputView.printException(exception.getErrorCode());
        }
    }
}
