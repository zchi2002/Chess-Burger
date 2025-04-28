import java.util.HashSet;
import java.util.List;

public abstract class Piece {
    protected boolean isWhite;
    protected boolean isUnmoved = true;

    public static class ValidMoves {
        private final HashSet<List<Integer>> quietMoves = new HashSet<>();
        private final HashSet<List<Integer>> capturingMoves = new HashSet<>();
        private final HashSet<List<Integer>> specialMoves = new HashSet<>();

        public HashSet<List<Integer>> getQuietMoves() {
            return quietMoves;
        }

        public HashSet<List<Integer>> getCapturingMoves() {
            return capturingMoves;
        }

        public HashSet<List<Integer>> getSpecialMoves() {
            return specialMoves;
        }
    }

    public Piece(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public boolean isUnmoved() {
        return isUnmoved;
    }

    public void setMoved() {
        isUnmoved = false;
    }

    public abstract ValidMoves getValidMoves(int x, int y, Board board);
}