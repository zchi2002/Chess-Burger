public class Bishop extends SlidingPiece {
    public Bishop(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public ValidMoves getValidMoves(int x, int y, Board board) {
        ValidMoves validMoves = new ValidMoves();

        // 只能在斜线方向上移动
        addValidMove(x, y, 1, 1, board, validMoves);
        addValidMove(x, y, 1, -1, board, validMoves);
        addValidMove(x, y, -1, 1, board, validMoves);
        addValidMove(x, y, -1, -1, board, validMoves);

        return validMoves;
    }
}