import javax.swing.JPanel;
import java.awt.*;

public class Board extends JPanel {

    public static final int ROWS = 3;
    public static final int COLS = 3;

    Cell[][] cells;

    public Board() {
        setPreferredSize(new Dimension(400, 400));
        cells = new Cell[ROWS][COLS];
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                cells[row][col] = new Cell(row, col);
            }
        }
    }

    public int getCellSize() {
        int width = getWidth();
        int height = getHeight();
        return (width < height ? width : height) / ROWS;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int cellSize = getCellSize();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                cells[row][col].paint(g, cellSize);
            }
        }
    }

    public void newGame() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                cells[row][col].content = Seed.NO_SEED;
            }
        }
    }

    public boolean isValidMove(int row, int col) {
        if (row >= 0 && row < ROWS && col >= 0 && col < COLS) {
            return cells[row][col].content == Seed.NO_SEED;
        }
        return false;
    }

    public GameStatus stepGame(Seed player, int row, int col) {
        cells[row][col].content = player;

        if (hasWon(player, row, col)) {
            return (player == Seed.CROSS) ? GameStatus.CROSS_WON : GameStatus.NOUGHT_WON;
        } else if (isDraw()) {
            return GameStatus.DRAW;
        } else {
            return GameStatus.PLAYING;
        }
    }

    private boolean isDraw() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (cells[row][col].content == Seed.NO_SEED) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean hasWon(Seed player, int row, int col) {
        if (cells[row][0].content == player && cells[row][1].content == player && cells[row][2].content == player) return true;
        if (cells[0][col].content == player && cells[1][col].content == player && cells[2][col].content == player) return true;
        if (row == col && cells[0][0].content == player && cells[1][1].content == player && cells[2][2].content == player) return true;
        if (row + col == 2 && cells[0][2].content == player && cells[1][1].content == player && cells[2][0].content == player) return true;
        return false;
    }

    public String serializeBoard() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                sb.append(cells[row][col].content.getSymbol());
            }
        }
        return sb.toString();
    }

    public void deserializeBoard(String boardState) {
        if (boardState == null || boardState.length() != ROWS * COLS) {
            return;
        }
        int i = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                char c = boardState.charAt(i++);
                if (c == 'X') cells[row][col].content = Seed.CROSS;
                else if (c == 'O') cells[row][col].content = Seed.NOUGHT;
                else cells[row][col].content = Seed.NO_SEED;
            }
        }
    }
}
