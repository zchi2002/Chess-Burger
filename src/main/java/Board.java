import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Board {
    private final Piece[][] board = new Piece[8][8];
    private List<Integer> kingPositionB, kingPositionW;
    private List<Integer> kingSideCastlingTargetPosition = null;
    private List<Integer> queenSideCastlingTargetPosition = null;
    private List<Integer> enPassantTargetPosition = null;
    private boolean isWhiteTurn = true, isSelected = false, isPromoting = false, isInCheck = false;
    private int selectedX = -1, selectedY = -1, promotingX = -1, promotingY = -1;
    HashSet<List<Integer>> lastMove = new HashSet<>(); // 上次移动的起点和终点
    Piece.ValidMoves currentValidMoves = new Piece.ValidMoves();

    public enum PromotionType {
        Knight, Bishop, Rook, Queen
    }

    public Board() {
        setupPieces();
    }

    private void setupPieces() {
        // 初始化兵
        for (int y = 0; y < 8; y++) {
            board[1][y] = new Pawn(false); // 黑方兵
            board[6][y] = new Pawn(true);  // 白方兵
        }

        // 初始化骑士
        board[0][1] = new Knight(false); // 黑方骑士
        board[0][6] = new Knight(false); // 黑方骑士
        board[7][1] = new Knight(true);  // 白方骑士
        board[7][6] = new Knight(true);  // 白方骑士

        // 初始化主教
        board[0][2] = new Bishop(false); // 黑方主教
        board[0][5] = new Bishop(false); // 黑方主教
        board[7][2] = new Bishop(true);  // 白方主教
        board[7][5] = new Bishop(true);  // 白方主教

        // 初始化城堡
        board[0][0] = new Rook(false); // 黑方城堡
        board[0][7] = new Rook(false); // 黑方城堡
        board[7][0] = new Rook(true);  // 白方城堡
        board[7][7] = new Rook(true);  // 白方城堡

        // 初始化皇后
        board[0][3] = new Queen(false); // 黑方皇后
        board[7][3] = new Queen(true);  // 白方皇后

        // 初始化国王
        board[0][4] = new King(false); // 黑方国王
        board[7][4] = new King(true);  // 白方国王
        kingPositionB = Arrays.asList(0, 4);
        kingPositionW = Arrays.asList(7, 4);
    }

    private boolean isKingThreatened() {
        List<Integer> kingPosition = isWhiteTurn ? kingPositionW : kingPositionB;
        Piece king = board[kingPosition.get(0)][kingPosition.get(1)];
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = board[x][y];
                if (piece != null && piece.isWhite() != king.isWhite()) {
                    Piece.ValidMoves validMoves = piece.getValidMoves(x, y, this);
                    if (validMoves.getCapturingMoves().contains(kingPosition)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isKingSideCastlingAvailable() {
        // 不能被将军
        if (isInCheck) {
            return false;
        }

        int x = isWhiteTurn ? 7 : 0;
        Piece piece;
        // 国王和城堡未移动过
        piece = board[x][4];
        if (!(piece instanceof King && piece.isUnmoved())) {
            return false;
        }
        piece = board[x][7];
        if (!(piece instanceof Rook && piece.isUnmoved())) {
            return false;
        }
        // 国王和城堡之间无其他棋子
        for (int y = 5; y <= 6; y++) {
            piece = board[x][y];
            if (piece != null) {
                return false;
            }
        }
        // 国王途经格和目标格不能处于对方攻击范围内
        for (int y = 5; y <= 6; y++) {
            board[x][y] = board[x][4]; // 此时board[x][y]必为null，无需备份
            board[x][4] = null;
            if (isWhiteTurn) {
                kingPositionW = Arrays.asList(x, y);
            } else {
                kingPositionB = Arrays.asList(x, y);
            }
            boolean isThreatened = isKingThreatened();
            board[x][4] = board[x][y];
            board[x][y] = null;
            if (isWhiteTurn) {
                kingPositionW = Arrays.asList(x, 4);
            } else {
                kingPositionB = Arrays.asList(x, 4);
            }
            if (isThreatened) {
                return false;
            }
        }

        return true;
    }

    private boolean isQueenSideCastlingAvailable() {
        // 不能被将军
        if (isInCheck) {
            return false;
        }

        int x = isWhiteTurn ? 7 : 0;
        Piece piece;
        // 国王和城堡未移动过
        piece = board[x][4];
        if (!(piece instanceof King && piece.isUnmoved())) {
            return false;
        }
        piece = board[x][0];
        if (!(piece instanceof Rook && piece.isUnmoved())) {
            return false;
        }
        // 国王和城堡之间无其他棋子
        for (int y = 3; y >= 1; y--) {
            piece = board[x][y];
            if (piece != null) {
                return false;
            }
        }
        // 国王途经格和目标格不能处于对方攻击范围内
        for (int y = 3; y >= 1; y--) {
            board[x][y] = board[x][4]; // 此时board[x][y]必为null，无需备份
            board[x][4] = null;
            if (isWhiteTurn) {
                kingPositionW = Arrays.asList(x, y);
            } else {
                kingPositionB = Arrays.asList(x, y);
            }
            boolean isThreatened = isKingThreatened();
            board[x][4] = board[x][y];
            board[x][y] = null;
            if (isWhiteTurn) {
                kingPositionW = Arrays.asList(x, 4);
            } else {
                kingPositionB = Arrays.asList(x, 4);
            }
            if (isThreatened) {
                return false;
            }
        }

        return true;
    }

    private void switchTurn() {
        isWhiteTurn = !isWhiteTurn;
        isInCheck = isKingThreatened();
        // 判断王车易位
        kingSideCastlingTargetPosition = null;
        if (isKingSideCastlingAvailable()) {
            kingSideCastlingTargetPosition = Arrays.asList(isWhiteTurn ? 7 : 0, 6);
        }
        queenSideCastlingTargetPosition = null;
        if (isQueenSideCastlingAvailable()) {
            queenSideCastlingTargetPosition = Arrays.asList(isWhiteTurn ? 7 : 0, 2);
        }
    }

    public void selectPiece(int x, int y) {
        Piece piece = board[x][y];
        if (piece != null && piece.isWhite() == isWhiteTurn) {
            isSelected = true;
            selectedX = x;
            selectedY = y;
            currentValidMoves = piece.getValidMoves(x, y, this);
        }
    }

    public void movePiece(int x, int y) {
        if (isSelected) {
            List<Integer> targetPosition = Arrays.asList(x, y);
            Piece piece = board[selectedX][selectedY];
            if (piece != null && (currentValidMoves.getQuietMoves().contains(targetPosition) || currentValidMoves.getCapturingMoves().contains(targetPosition) || currentValidMoves.getSpecialMoves().contains(targetPosition))) {
                Piece backupPiece1 = board[x][y], backupPiece2 = null;
                if (piece instanceof Pawn && targetPosition.equals(enPassantTargetPosition)) {
                    backupPiece2 = board[x + (isWhiteTurn ? 1 : -1)][y];
                    board[x][y] = piece;
                    board[selectedX][selectedY] = null;
                    board[x + (isWhiteTurn ? 1 : -1)][y] = null;
                } else if (piece instanceof King) {
                    if (targetPosition.equals(kingSideCastlingTargetPosition)) {
                        backupPiece2 = board[x][5];
                        board[x][y] = piece;
                        board[selectedX][selectedY] = null;
                        board[x][5] = board[x][7];
                        board[x][7] = null;
                    } else if (targetPosition.equals(queenSideCastlingTargetPosition)) {
                        backupPiece2 = board[x][3];
                        board[x][y] = piece;
                        board[selectedX][selectedY] = null;
                        board[x][3] = board[x][0];
                        board[x][0] = null;
                    } else {
                        board[x][y] = piece;
                        board[selectedX][selectedY] = null;
                    }
                    if (isWhiteTurn) {
                        kingPositionW = targetPosition;
                    } else {
                        kingPositionB = targetPosition;
                    }
                } else {
                    board[x][y] = piece;
                    board[selectedX][selectedY] = null;
                }
                // 如果国王将处于对方攻击范围内，则本次移动无效
                if (isKingThreatened()) {
                    if (piece instanceof Pawn && targetPosition.equals(enPassantTargetPosition)) {
                        board[selectedX][selectedY] = piece;
                        board[x][y] = backupPiece1;
                        board[x + (isWhiteTurn ? 1 : -1)][y] = backupPiece2;
                    } else if (piece instanceof King) {
                        if (targetPosition.equals(kingSideCastlingTargetPosition)) {
                            board[selectedX][selectedY] = piece;
                            board[x][y] = backupPiece1;
                            board[x][7] = board[x][5];
                            board[x][5] = backupPiece2;
                        } else if (targetPosition.equals(queenSideCastlingTargetPosition)) {
                            board[selectedX][selectedY] = piece;
                            board[x][y] = backupPiece1;
                            board[x][0] = board[x][3];
                            board[x][3] = backupPiece2;
                        } else {
                            board[selectedX][selectedY] = piece;
                            board[x][y] = backupPiece1;
                        }
                        if (isWhiteTurn) {
                            kingPositionW = Arrays.asList(selectedX, selectedY);
                        } else {
                            kingPositionB = Arrays.asList(selectedX, selectedY);
                        }
                    } else {
                        board[selectedX][selectedY] = piece;
                        board[x][y] = backupPiece1;
                    }
                } else {
                    if (piece.isUnmoved()) {
                        piece.setMoved();
                    }
                    // 参与王车易位的城堡也认为被移动过
                    if (piece instanceof King) {
                        if (targetPosition.equals(kingSideCastlingTargetPosition)) {
                            board[x][5].setMoved();
                        } else if (targetPosition.equals(queenSideCastlingTargetPosition)) {
                            board[x][3].setMoved();
                        }
                    }
                    lastMove.clear();
                    lastMove.add(Arrays.asList(selectedX, selectedY));
                    lastMove.add(targetPosition);
                    // 判断吃过路兵与升变
                    enPassantTargetPosition = null;
                    if (piece instanceof Pawn) {
                        if (Math.abs(x - selectedX) == 2) {
                            enPassantTargetPosition = Arrays.asList((x + selectedX) / 2, y);
                        }
                        if ((piece.isWhite() && x == 0) || (!piece.isWhite() && x == 7)) {
                            isPromoting = true;
                            promotingX = x;
                            promotingY = y;
                        }
                    }
                    if (!isPromoting) {
                        switchTurn();
                    }
                }
            }
            isSelected = false;
            selectedX = -1;
            selectedY = -1;
        }
    }

    public void promotePawn(PromotionType promotionType) {
        // 升变后的棋子认为被移动过
        switch (promotionType) {
            case Knight:
                board[promotingX][promotingY] = new Knight(isWhiteTurn);
                board[promotingX][promotingY].setMoved();
                break;
            case Bishop:
                board[promotingX][promotingY] = new Bishop(isWhiteTurn);
                board[promotingX][promotingY].setMoved();
                break;
            case Rook:
                board[promotingX][promotingY] = new Rook(isWhiteTurn);
                board[promotingX][promotingY].setMoved();
                break;
            case Queen:
                board[promotingX][promotingY] = new Queen(isWhiteTurn);
                board[promotingX][promotingY].setMoved();
                break;
        }
        isPromoting = false;
        promotingX = -1;
        promotingY = -1;
        switchTurn();
    }

    public Piece getPiece(int x, int y) {
        return board[x][y];
    }

    public List<Integer> getKingPositionB() {
        return kingPositionB;
    }

    public List<Integer> getKingPositionW() {
        return kingPositionW;
    }

    public List<Integer> getKingSideCastlingTargetPosition() {
        return kingSideCastlingTargetPosition;
    }

    public List<Integer> getQueenSideCastlingTargetPosition() {
        return queenSideCastlingTargetPosition;
    }

    public List<Integer> getEnPassantTargetPosition() {
        return enPassantTargetPosition;
    }

    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public boolean isPromoting() {
        return isPromoting;
    }

    public boolean isInCheck() {
        return isInCheck;
    }

    public HashSet<List<Integer>> getLastMove() {
        return lastMove;
    }

    public Piece.ValidMoves getCurrentValidMoves() {
        return currentValidMoves;
    }
}