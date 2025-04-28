import java.util.Arrays;
import java.util.List;

public class Pawn extends Piece {
    public Pawn(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public ValidMoves getValidMoves(int x, int y, Board board) {
        ValidMoves validMoves = new ValidMoves();

        int direction = isWhite ? -1 : 1;  // 白方走 -1，黑方走 +1
        int targetX, targetY;
        List<Integer> targetPosition;
        Piece targetPiece;

        // 直线走棋
        targetX = x;
        targetY = y;
        for (int i = 0; i < 2; ++i) {
            targetX += direction;
            if (targetX >= 0 && targetX <= 7) {
                targetPosition = Arrays.asList(targetX, targetY);
                targetPiece = board.getPiece(targetX, targetY);
                if (targetPiece == null) {
                    validMoves.getQuietMoves().add(targetPosition);
                    if (!isUnmoved) {
                        break;
                    }
                } else {
                    break;
                }
            } else {
                break;
            }
        }

        // 斜线吃子，包括吃过路兵
        targetX = x + direction;
        for (int dy : new int[]{1, -1}) {
            targetY = y + dy;
            if (targetX >= 0 && targetX <= 7 && targetY >= 0 && targetY <= 7) {
                targetPosition = Arrays.asList(targetX, targetY);
                targetPiece = board.getPiece(targetX, targetY);
                if (targetPiece != null && targetPiece.isWhite() != this.isWhite()) {
                    validMoves.getCapturingMoves().add(targetPosition);
                } else if (targetPiece == null && targetPosition.equals(board.getEnPassantTargetPosition())) {
                    validMoves.getSpecialMoves().add(targetPosition);
                }
            }
        }

        return validMoves;
    }
}