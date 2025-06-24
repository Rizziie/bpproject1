import java.util.Scanner;

public class Game {
    private static PlayerType currentPlayer;
    private static TTTState currentState;
    private static Board board = new Board();
    private static final Scanner in = new Scanner(System.in);

    public void start() {
        Greeting.show();
        do {
            board.init();
            currentPlayer = PlayerType.CROSS;
            currentState = TTTState.PLAYING;

            do {
                stepGame();
                board.paint();

                if (currentState == TTTState.CROSS_WON) {
                    System.out.println("'X' won!");
                } else if (currentState == TTTState.NOUGHT_WON) {
                    System.out.println("'O' won!");
                } else if (currentState == TTTState.DRAW) {
                    System.out.println("It's a Draw!");
                } else {
                    currentPlayer = (currentPlayer == PlayerType.CROSS) ? PlayerType.NOUGHT : PlayerType.CROSS;
                }
            } while (currentState == TTTState.PLAYING);

            System.out.print("Play again (y/n)? ");
            char ans = in.next().charAt(0);
            if (ans != 'y' && ans != 'Y') {
                System.out.println("Bye!");
                break;
            }
        } while (true);
    }

    public static void stepGame() {
        boolean validInput = false;
        do {
            System.out.printf("Player '%s', enter your move (row[1-3] column[1-3]): ", currentPlayer);
            int row = in.nextInt() - 1;
            int col = in.nextInt() - 1;
            if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS && board.isEmptyCell(row, col)) {
                board.setCell(row, col, currentPlayer);
                currentState = updateGameState(row, col);
                validInput = true;
            } else {
                System.out.println("Invalid move. Try again...");
            }
        } while (!validInput);
    }

    public static TTTState updateGameState(int row, int col) {
        PlayerType[][] b = board.getBoard();

        if (
                b[row][0] == currentPlayer && b[row][1] == currentPlayer && b[row][2] == currentPlayer ||
                        b[0][col] == currentPlayer && b[1][col] == currentPlayer && b[2][col] == currentPlayer ||
                        row == col && b[0][0] == currentPlayer && b[1][1] == currentPlayer && b[2][2] == currentPlayer ||
                        row + col == 2 && b[0][2] == currentPlayer && b[1][1] == currentPlayer && b[2][0] == currentPlayer
        ) {
            return (currentPlayer == PlayerType.CROSS) ? TTTState.CROSS_WON : TTTState.NOUGHT_WON;
        }

        for (int r = 0; r < Board.ROWS; ++r) {
            for (int c = 0; c < Board.COLS; ++c) {
                if (b[r][c] == PlayerType.NO_SEED) return TTTState.PLAYING;
            }
        }

        return TTTState.DRAW;
    }
}
