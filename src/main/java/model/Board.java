package model;

import constant.ErrorCode;
import exception.InvalidTurnException;
import exception.KingDeadException;
import exception.PieceDoesNotExistException;
import exception.PieceExistInRouteException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import model.piece.Bishop;
import model.piece.BlackPawn;
import model.piece.King;
import model.piece.Knight;
import model.piece.Pawn;
import model.piece.Piece;
import model.piece.Queen;
import model.piece.Rook;
import model.piece.WhitePawn;
import model.position.File;
import model.position.Moving;
import model.position.Position;
import model.position.Rank;

public class Board {

    private static final Map<File, Function<Camp, Piece>> startingPosition = Map.of(
            File.A, Rook::new,
            File.B, Knight::new,
            File.C, Bishop::new,
            File.D, Queen::new,
            File.E, King::new,
            File.F, Bishop::new,
            File.G, Knight::new,
            File.H, Rook::new
    );
    private static final Set<Piece> KINGS = Set.of(new King(Camp.WHITE), new King(Camp.BLACK));
    private static final Set<Piece> PAWNS = Set.of(new WhitePawn(), new BlackPawn());
    private static final Score SAME_FILE_PAWN_SCORE = new Score(0.5F);

    private final Map<Position, Piece> pieces;

    public Board(final Map<Position, Piece> pieces) {
        this.pieces = new HashMap<>(pieces);
    }

    public static Board create() {
        final Map<Position, Piece> result = new HashMap<>();
        settingExceptPawn(result, Camp.BLACK, Rank.EIGHT);
        settingPawn(result, Rank.SEVEN, new BlackPawn());
        settingPawn(result, Rank.TWO, new WhitePawn());
        settingExceptPawn(result, Camp.WHITE, Rank.ONE);
        return new Board(result);
    }

    private static void settingExceptPawn(final Map<Position, Piece> board, final Camp camp, final Rank rank) {
        for (File file : File.values()) {
            final Function<Camp, Piece> createFunction = startingPosition.get(file);
            final Piece piece = createFunction.apply(camp);
            board.put(new Position(file, rank), piece);
        }
    }

    private static void settingPawn(final Map<Position, Piece> board, final Rank rank, final Pawn pawn) {
        for (File file : File.values()) {
            board.put(new Position(file, rank), pawn);
        }
    }

    public void validate(final Moving moving, final Camp currentCamp) {
        final Position currentPosition = moving.getCurrentPosition();
        validateExistPiece(currentPosition);
        final Piece piece = pieces.get(currentPosition);
        validateOwnPiece(currentCamp, piece);
        final Set<Position> route = getRoute(moving, piece);
        validateUnBlocked(route);
        final Position nextPosition = moving.getNextPosition();
        validateTargetEnemy(currentCamp, nextPosition);
        validateIsKing(nextPosition);
    }

    private void validateIsKing(final Position nextPosition) {
        if (!pieces.containsKey(nextPosition)) {
            return;
        }
        final Piece piece = pieces.get(nextPosition);
        if (KINGS.contains(piece)) {
            throw new KingDeadException(ErrorCode.KING_DEAD);
        }
    }

    private void validateExistPiece(final Position currentPosition) {
        if (!pieces.containsKey(currentPosition)) {
            throw new PieceDoesNotExistException(ErrorCode.PIECE_DOES_NOT_EXIST_POSITION);
        }
    }

    private void validateOwnPiece(final Camp currentCamp, final Piece piece) {
        if (!piece.isSameCamp(currentCamp)) {
            throw new InvalidTurnException(ErrorCode.INVALID_CAMP_PIECE);
        }
    }

    private void validateUnBlocked(final Set<Position> route) {
        boolean blocked = route.stream()
                .anyMatch(pieces::containsKey);
        if (blocked) {
            throw new PieceExistInRouteException(ErrorCode.PIECE_EXIST_IN_ROUTE);
        }
    }

    private void validateTargetEnemy(final Camp currentCamp, final Position nextPosition) {
        if (pieces.containsKey(nextPosition) && pieces.get(nextPosition).isSameCamp(currentCamp)) {
            throw new PieceExistInRouteException(ErrorCode.OWN_PIECE_EXIST_POSITION);
        }
    }

    private Set<Position> getRoute(final Moving moving, final Piece piece) {
        if (pieces.containsKey(moving.getNextPosition())) {
            return piece.getAttackRoute(moving);
        }
        return piece.getMoveRoute(moving);
    }

    public void move(final Moving moving) {
        final Piece piece = pieces.get(moving.getCurrentPosition());
        pieces.put(moving.getNextPosition(), piece);
        pieces.remove(moving.getCurrentPosition());
    }

    public Score calculateScore(final Camp camp) {
        final Map<File, Integer> pawnCount = countSameFilePawn(camp);
        final List<Score> scores = collectScore(camp);
        final Score result = scores.stream()
                .reduce(Score::plus)
                .orElse(new Score(0));

        return result.minus(duplicateFilePawns(pawnCount));
    }

    private Map<File, Integer> countSameFilePawn(final Camp camp) {
        final Map<File, Integer> pawnCount = new EnumMap<>(File.class);
        pieces.entrySet()
                .stream()
                .filter(entry -> entry.getValue().isSameCamp(camp))
                .forEach(entry -> checkPawn(entry, pawnCount));
        return pawnCount;
    }

    private void checkPawn(final Entry<Position, Piece> entry, final Map<File, Integer> count) {
        final Piece piece = entry.getValue();
        if (PAWNS.contains(piece)) {
            final Position position = entry.getKey();
            final File file = position.getFile();
            count.put(file, count.getOrDefault(file, 0) + 1);
        }
    }

    private List<Score> collectScore(final Camp camp) {
        return pieces.values()
                .stream()
                .filter(piece -> piece.isSameCamp(camp))
                .map(PieceScore::getScore)
                .toList();
    }

    private Score duplicateFilePawns(final Map<File, Integer> count) {
        return count.values()
                .stream()
                .filter(sameFilePawnCount -> sameFilePawnCount > 1)
                .map(sameFilePawnCount -> new Score(sameFilePawnCount * SAME_FILE_PAWN_SCORE.value()))
                .reduce(Score::plus)
                .orElse(new Score(0));
    }

    public Map<Position, Piece> getPieces() {
        return Collections.unmodifiableMap(pieces);
    }
}
