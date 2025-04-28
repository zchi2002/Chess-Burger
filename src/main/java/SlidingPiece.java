import java.util.Arrays;
import java.util.List;

public abstract class SlidingPiece extends Piece {
    public SlidingPiece(boolean isWhite) {
        super(isWhite);
    }

    protected void addValidMove(int x, int y, int xDirection, int yDirection, Board board, ValidMoves validMoves) {
        for (int targetX = x + xDirection, targetY = y + yDirection; targetX >= 0 && targetX <= 7 && targetY >= 0 && targetY <= 7; targetX += xDirection, targetY += yDirection) {
            List<Integer> targetPosition = Arrays.asList(targetX, targetY);
            Piece targetPiece = board.getPiece(targetX, targetY);
            if (targetPiece == null) {
                validMoves.getQuietMoves().add(targetPosition);
            } else {
                if (targetPiece.isWhite() != this.isWhite()) {
                    validMoves.getCapturingMoves().add(targetPosition);
                }
                break;
            }
        }
    }
}