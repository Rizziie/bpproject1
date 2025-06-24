import javax.swing.*;
import java.awt.*;

public class TTTPanel extends JPanel {

    private final TTTGame game;

    public static final int CELL_SIZE = 120;
    public static final int GRID_WIDTH = 10;
    public static final int SYMBOL_STROKE_WIDTH = 8;
    public static final int CELL_PADDING = CELL_SIZE / 5;
    public static final int SYMBOL_SIZE = CELL_SIZE - CELL_PADDING * 2;

    public static final Color COLOR_GRID = Color.LIGHT_GRAY;
    public static final Color COLOR_CROSS = new Color(211, 45, 65);
    public static final Color COLOR_NOUGHT = new Color(76, 181, 245);
    public static final Color COLOR_BG = Color.WHITE;

    public TTTPanel(TTTGame game) {
        this.game = game;
        setPreferredSize(new Dimension(CELL_SIZE * TTTGame.COLS, CELL_SIZE * TTTGame.ROWS));
        setBackground(COLOR_BG);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(COLOR_GRID);
        for (int row = 1; row < TTTGame.ROWS; row++) {
            g.fillRect(0, row * CELL_SIZE - GRID_WIDTH / 2, getWidth(), GRID_WIDTH);
        }
        for (int col = 1; col < TTTGame.COLS; col++) {
            g.fillRect(col * CELL_SIZE - GRID_WIDTH / 2, 0, GRID_WIDTH, getHeight());
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(SYMBOL_STROKE_WIDTH));

        PlayerType[][] board = game.getBoard();
        for (int row = 0; row < TTTGame.ROWS; row++) {
            for (int col = 0; col < TTTGame.COLS; col++) {
                int x1 = col * CELL_SIZE + CELL_PADDING;
                int y1 = row * CELL_SIZE + CELL_PADDING;
                if (board[row][col] == PlayerType.CROSS) {
                    g2d.setColor(COLOR_CROSS);
                    int x2 = (col + 1) * CELL_SIZE - CELL_PADDING;
                    int y2 = (row + 1) * CELL_SIZE - CELL_PADDING;
                    g2d.drawLine(x1, y1, x2, y2);
                    g2d.drawLine(x2, y1, x1, y2);
                } else if (board[row][col] == PlayerType.NOUGHT) {
                    g2d.setColor(COLOR_NOUGHT);
                    g2d.drawOval(x1, y1, SYMBOL_SIZE, SYMBOL_SIZE);
                }
            }
        }
    }
}
