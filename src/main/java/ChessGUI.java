import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ChessGUI extends JFrame {
    private final Board board;
    private final JButton[][] buttons = new JButton[8][10];
    private JButton selectedButton = null;  // 用于存储当前选中的按钮

    public ChessGUI() {
        board = new Board();
        setTitle("Chess Game");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(8, 10));

        initializeBoard();
        refreshBoard();
        setVisible(true);
    }

    private void initializeBoard() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 10; y++) {
                JButton button = new JButton();
                int finalX = x, finalY = y;
                button.addActionListener(e -> handleClick(finalX, finalY));
                button.setBorderPainted(false);
                button.setFocusPainted(false);
                button.setContentAreaFilled(false);  // 禁用默认填充
                button.setOpaque(true);
                if (y == 8) {
                    button.setFont(new Font("Segoe UI Symbol", Font.BOLD, 30));
                } else {
                    button.setFont(new Font("Segoe UI Symbol", Font.BOLD, 60));
                }
                buttons[x][y] = button;
                add(button);
            }
        }
    }

    private void handleClick(int x, int y) {
        if (!board.isPromoting() && x >= 0 && x <= 7 && y >= 0 && y <= 7) {
            Piece piece = board.getPiece(x, y);
            if (!board.isSelected()) {
                // 第一次点击（选中行动方棋子）
                if (piece != null && piece.isWhite() == board.isWhiteTurn()) {
                    selectedButton = buttons[x][y];
                    board.selectPiece(x, y);
                    refreshBoard();
                }
            } else {
                // 第二次点击（行动方重新选棋子，或尝试移动棋子并取消选择）
                if (piece != null && piece.isWhite() == board.isWhiteTurn()) {
                    selectedButton = buttons[x][y];
                    board.selectPiece(x, y);
                    refreshBoard();
                } else {
                    selectedButton = null;
                    board.movePiece(x, y);
                    refreshBoard();
                }
            }
        }
        if (board.isPromoting() && x >= 0 && x <= 3 && y == 9) {
            switch (x) {
                case 0:
                    board.promotePawn(Board.PromotionType.Knight);
                    break;
                case 1:
                    board.promotePawn(Board.PromotionType.Bishop);
                    break;
                case 2:
                    board.promotePawn(Board.PromotionType.Rook);
                    break;
                case 3:
                    board.promotePawn(Board.PromotionType.Queen);
                    break;
            }
            refreshBoard();
        }
    }

    private void refreshBoard() {
        List<Integer> positionInCheck;
        if (board.isInCheck()) {
            if (board.isWhiteTurn()) {
                positionInCheck = board.getKingPositionW();
            } else {
                positionInCheck = board.getKingPositionB();
            }
        } else {
            positionInCheck = null;
        }
        HashSet<List<Integer>> lastMove = board.getLastMove();
        Piece.ValidMoves currentValidMoves = board.isSelected() ? board.getCurrentValidMoves() : new Piece.ValidMoves();

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                List<Integer> position = Arrays.asList(x, y);
                Piece piece = board.getPiece(x, y);
                JButton button = buttons[x][y];
                if (piece == null) {
                    button.setText("");  // 如果没有棋子，清空文字
                } else {
                    // 根据棋子类型选择显示字符
                    if (piece instanceof Pawn) {
                        button.setText(piece.isWhite() ? "♙" : "♟");
                    } else if (piece instanceof Knight) {
                        button.setText(piece.isWhite() ? "♘" : "♞");
                    } else if (piece instanceof Bishop) {
                        button.setText(piece.isWhite() ? "♗" : "♝");
                    } else if (piece instanceof Rook) {
                        button.setText(piece.isWhite() ? "♖" : "♜");
                    } else if (piece instanceof Queen) {
                        button.setText(piece.isWhite() ? "♕" : "♛");
                    } else if (piece instanceof King) {
                        button.setText(piece.isWhite() ? "♔" : "♚");
                    }
                }

                // 设置格子的背景颜色
                if ((x + y) % 2 == 0) {
                    button.setBackground(Color.WHITE);
                } else {
                    button.setBackground(new Color(0x4B7F4B));
                }
                if (position.equals(positionInCheck)) {
                    button.setBackground(Color.RED);
                }

                // 为格子添加边框
                button.setBorderPainted(false);
                if (button == selectedButton) {
                    button.setBorderPainted(true);
                    button.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 8));
                }
                if (lastMove.contains(position)) {
                    button.setBorderPainted(true);
                    button.setBorder(BorderFactory.createLineBorder(Color.BLUE, 8));
                }
                if (currentValidMoves.getQuietMoves().contains(position)) {
                    button.setBorderPainted(true);
                    button.setBorder(BorderFactory.createLineBorder(Color.GREEN, 8));
                }
                if (currentValidMoves.getCapturingMoves().contains(position)) {
                    button.setBorderPainted(true);
                    button.setBorder(BorderFactory.createLineBorder(Color.RED, 8));
                }
                if (currentValidMoves.getSpecialMoves().contains(position)) {
                    button.setBorderPainted(true);
                    button.setBorder(BorderFactory.createLineBorder(Color.MAGENTA, 8));
                }
            }
        }

        buttons[0][8].setText(board.isWhiteTurn() ? "W" : "B");
        buttons[0][8].setBorderPainted(true);
        buttons[0][8].setBorder(BorderFactory.createLineBorder(Color.GRAY, 8));

        if (board.isPromoting()) {
            buttons[0][9].setText(board.isWhiteTurn() ? "♘" : "♞");
            buttons[1][9].setText(board.isWhiteTurn() ? "♗" : "♝");
            buttons[2][9].setText(board.isWhiteTurn() ? "♖" : "♜");
            buttons[3][9].setText(board.isWhiteTurn() ? "♕" : "♛");
            for (int x = 0; x < 4; x++) {
                buttons[x][9].setBorderPainted(true);
                buttons[x][9].setBorder(BorderFactory.createLineBorder(Color.GRAY, 8));
            }
        } else {
            for (int x = 0; x < 4; x++) {
                buttons[x][9].setText("");
                buttons[x][9].setBorderPainted(false);
            }
        }
    }
}