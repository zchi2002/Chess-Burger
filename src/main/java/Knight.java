public class Knight extends SteppingPiece {
    public Knight(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public ValidMoves getValidMoves(int x, int y, Board board) {
        ValidMoves validMoves = new ValidMoves();

        // 骑士的移动是L型的，即横向或纵向移动两格，然后再纵向或横向移动一格
        addValidMove(x, y, 1, 2, board, validMoves);
        addValidMove(x, y, 2, 1, board, validMoves);
        addValidMove(x, y, 1, -2, board, validMoves);
        addValidMove(x, y, 2, -1, board, validMoves);
        addValidMove(x, y, -1, 2, board, validMoves);
        addValidMove(x, y, -2, 1, board, validMoves);
        addValidMove(x, y, -1, -2, board, validMoves);
        addValidMove(x, y, -2, -1, board, validMoves);

        return validMoves;
    }
}