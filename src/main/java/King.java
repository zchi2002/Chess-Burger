import java.util.List;

public class King extends SteppingPiece {
    public King(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public ValidMoves getValidMoves(int x, int y, Board board) {
        ValidMoves validMoves = new ValidMoves();

        // 国王的移动范围是周围八格
        addValidMove(x, y, 1, 1, board, validMoves);
        addValidMove(x, y, 1, 0, board, validMoves);
        addValidMove(x, y, 1, -1, board, validMoves);
        addValidMove(x, y, 0, 1, board, validMoves);
        addValidMove(x, y, 0, -1, board, validMoves);
        addValidMove(x, y, -1, 1, board, validMoves);
        addValidMove(x, y, -1, 0, board, validMoves);
        addValidMove(x, y, -1, -1, board, validMoves);

        // 王车易位
        List<Integer> kingSideCastlingTargetPosition = board.getKingSideCastlingTargetPosition();
        if (kingSideCastlingTargetPosition != null) {
            validMoves.getSpecialMoves().add(kingSideCastlingTargetPosition);
        }
        List<Integer> queenSideCastlingTargetPosition = board.getQueenSideCastlingTargetPosition();
        if (queenSideCastlingTargetPosition != null) {
            validMoves.getSpecialMoves().add(queenSideCastlingTargetPosition);
        }

        return validMoves;
    }
}