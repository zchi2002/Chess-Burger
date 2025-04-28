import java.util.Arrays;
import java.util.List;

public abstract class SteppingPiece extends Piece {
    public SteppingPiece(boolean isWhite) {
        super(isWhite);
    }

    protected void addValidMove(int x, int y, int dx, int dy, Board board, ValidMoves validMoves) {
        int targetX = x + dx;
        int targetY = y + dy;

        if (targetX >= 0 && targetX <= 7 && targetY >= 0 && targetY <= 7) {
            List<Integer> targetPosition = Arrays.asList(targetX, targetY);
            Piece targetPiece = board.getPiece(targetX, targetY);
            if (targetPiece == null) {
                validMoves.getQuietMoves().add(targetPosition);
            } else if (targetPiece.isWhite() != this.isWhite()) {
                validMoves.getCapturingMoves().add(targetPosition);
            }
        }
    }
}