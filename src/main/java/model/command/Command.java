package model.command;

import constant.ErrorCode;
import exception.InvalidCommandException;
import java.util.Arrays;
import java.util.regex.Pattern;

public enum Command {

    START("start", 0),
    MOVE("move", 2),
    POSITION("[a-hA-H][1-8]", 0),
    STATUS("status", 0),
    QUIT("quit", 0),
    END("end", 0);

    private final Pattern pattern;
    private final int bodySize;

    Command(final String regex, final int bodySize) {
        this.pattern = Pattern.compile(regex);
        this.bodySize = bodySize;
    }

    public static Command from(final String value) {
        return Arrays.stream(values())
                .filter(command -> command.pattern.matcher(value).matches())
                .findFirst()
                .orElseThrow(() -> new InvalidCommandException(ErrorCode.INVALID_COMMAND));
    }

    public boolean isEqualToBodySize(final int targetSize) {
        return bodySize == targetSize;
    }
}
