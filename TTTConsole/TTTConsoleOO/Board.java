public class Board {
    public static final int ROWS = 3, COLS = 3;
    private PlayerType[][] board = new PlayerType[ROWS][COLS];

    public void init() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                board[row][col] = PlayerType.NO_SEED;
            }
        }
    }

    public boolean isEmptyCell(int row, int col) {
        return board[row][col] == PlayerType.NO_SEED;
    }

    public void setCell(int row, int col, PlayerType player) {
        board[row][col] = player;
    }

    public PlayerType[][] getBoard() {
        return board;
    }

    public void paint() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                System.out.print(" " + board[row][col] + " ");
                if (col != COLS - 1) System.out.print("|");
            }
            System.out.println();
            if (row != ROWS - 1) System.out.println("-----------");
        }
        System.out.println();
    }
}
