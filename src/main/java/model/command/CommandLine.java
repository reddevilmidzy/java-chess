package model.command;

import constant.ErrorCode;
import exception.InvalidCommandException;
import java.util.Collections;
import java.util.List;

public class CommandLine {

    public static final int CURRENT_POSITION_INDEX = 0;
    public static final int NEXT_POSITION_INDEX = 1;
    private static final int HEAD_INDEX = 0;

    private final Command head;
    private final List<String> body;

    private CommandLine(final Command head, final List<String> body) {
        this.head = head;
        this.body = body;
    }

    public static CommandLine from(final List<String> values) {
        validateEmpty(values);
        validateCommand(values);
        Command command = Command.from(values.get(HEAD_INDEX));
        validateSize(command, values);
        return new CommandLine(command, values.subList(1, values.size()));
    }

    private static void validateEmpty(final List<String> values) {
        if (values == null || values.isEmpty()) {
            throw new InvalidCommandException(ErrorCode.INVALID_COMMAND);
        }
    }

    private static void validateCommand(final List<String> values) {
        values.forEach(Command::from);
    }

    private static void validateSize(final Command command, final List<String> values) {
        if (!command.isEqualToBodySize(values.size() - 1)) {
            throw new InvalidCommandException(ErrorCode.INVALID_COMMAND);
        }
    }

    public boolean isStart() {
        return head == Command.START;
    }

    public boolean isEnd() {
        return head == Command.END;
    }

    public boolean isMove() {
        return head == Command.MOVE;
    }

    public boolean isStatus() {
        return head == Command.STATUS;
    }

    public boolean isQuit() {
        return head == Command.QUIT;
    }

    public List<String> getBody() {
        return Collections.unmodifiableList(body);
    }
}
