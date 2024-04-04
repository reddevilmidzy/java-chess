package view;

import constant.ErrorCode;
import exception.InvalidInputException;
import java.util.List;
import java.util.Scanner;

public class InputView {

    private static final Scanner scanner = new Scanner(System.in);

    private InputView() {
    }

    public static List<String> readCommandList() {
        final String rawCommand = scanner.nextLine();
        validate(rawCommand);
        return List.of(rawCommand.split(" "));
    }

    private static void validate(final String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidInputException(ErrorCode.INVALID_INPUT);
        }
    }
}
