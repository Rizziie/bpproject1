public class TTTGame {

    public static final int ROWS = 3;
    public static final int COLS = 3;

    private PlayerType[][] board;
    private PlayerType currentPlayer;
    private GameState currentState;

    public TTTGame() {
        board = new PlayerType[ROWS][COLS];
        newGame();
    }

    public void newGame() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                board[row][col] = PlayerType.NO_SEED;
            }
        }
        currentPlayer = PlayerType.CROSS;
        currentState = GameState.PLAYING;
    }

    public GameState makeMove(int row, int col) {
        if (board[row][col] == PlayerType.NO_SEED) {
            board[row][col] = currentPlayer;
            if (hasWon(currentPlayer, row, col)) {
                currentState = (currentPlayer == PlayerType.CROSS) ? GameState.CROSS_WON : GameState.NOUGHT_WON;
            } else if (isDraw()) {
                currentState = GameState.DRAW;
            } else {
                currentPlayer = (currentPlayer == PlayerType.CROSS) ? PlayerType.NOUGHT : PlayerType.CROSS;
            }
        }
        return currentState;
    }

    private boolean hasWon(PlayerType player, int row, int col) {
        return (board[row][0] == player && board[row][1] == player && board[row][2] == player) ||
                (board[0][col] == player && board[1][col] == player && board[2][col] == player) ||
                (row == col && board[0][0] == player && board[1][1] == player && board[2][2] == player) ||
                (row + col == 2 && board[0][2] == player && board[1][1] == player && board[2][0] == player);
    }

    private boolean isDraw() {
        for (int row = 0; row < ROWS; row++)
            for (int col = 0; col < COLS; col++)
                if (board[row][col] == PlayerType.NO_SEED)
                    return false;
        return true;
    }

    public PlayerType[][] getBoard() {
        return board;
    }

    public PlayerType getCurrentPlayer() {
        return currentPlayer;
    }

    public GameState getCurrentState() {
        return currentState;
    }
}