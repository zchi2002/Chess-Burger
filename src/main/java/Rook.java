public class Rook extends SlidingPiece {
    public Rook(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public ValidMoves getValidMoves(int x, int y, Board board) {
        ValidMoves validMoves = new ValidMoves();

        // 只能在直线方向上移动
        addValidMove(x, y, 1, 0, board, validMoves);
        addValidMove(x, y, -1, 0, board, validMoves);
        addValidMove(x, y, 0, 1, board, validMoves);
        addValidMove(x, y, 0, -1, board, validMoves);

        return validMoves;
    }
}