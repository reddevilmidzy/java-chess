package application;

import controller.ChessController;
import db.exception.DbException;
import java.util.Scanner;
import view.InputView;
import view.OutputView;

public class Application {

    public static void main(String[] args) {
        final InputView inputView = new InputView(new Scanner(System.in));
        final OutputView outputView = new OutputView();

        final ChessController chessController = new ChessController(inputView, outputView);
        try {
            chessController.run();
        } catch (DbException exception) {
            outputView.printException(exception.getErrorCode());
        }
    }
}
