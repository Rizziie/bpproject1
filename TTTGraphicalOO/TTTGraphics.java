import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TTTGraphics extends JFrame {

    private final TTTGame game;
    private final TTTPanel gamePanel;
    private final JLabel statusBar;

    public TTTGraphics() {
        JOptionPane.showMessageDialog(this,
                "Welcome to our OOP Tic-Tac-Toe!\n\n" +
                        "If you are X, you go first.\n" +
                        "Click on any cell to start.\n" +
                        "Click anywhere on the board after the game ends to restart.",
                "Welcome", JOptionPane.INFORMATION_MESSAGE);

        game = new TTTGame();
        gamePanel = new TTTPanel(game);
        statusBar = new JLabel(" ");
        statusBar.setFont(new Font("OCR A Extended", Font.PLAIN, 14));
        statusBar.setOpaque(true);
        statusBar.setBackground(new Color(216, 216, 216));
        updateStatus();

        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = e.getY() / TTTPanel.CELL_SIZE;
                int col = e.getX() / TTTPanel.CELL_SIZE;

                if (game.getCurrentState() == GameState.PLAYING) {
                    game.makeMove(row, col);
                } else {
                    game.newGame();
                }

                gamePanel.repaint();
                updateStatus();

                if (game.getCurrentState() == GameState.CROSS_WON) {
                    JOptionPane.showMessageDialog(TTTGraphics.this, "'X' Won! Click OK to restart.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                } else if (game.getCurrentState() == GameState.NOUGHT_WON) {
                    JOptionPane.showMessageDialog(TTTGraphics.this, "'O' Won! Click OK to restart.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                } else if (game.getCurrentState() == GameState.DRAW) {
                    JOptionPane.showMessageDialog(TTTGraphics.this, "It's a Draw! Click OK to restart.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        setLayout(new BorderLayout());
        add(gamePanel, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setTitle("Tic Tac Toe");
        setVisible(true);
    }

    private void updateStatus() {
        if (game.getCurrentState() == GameState.PLAYING) {
            statusBar.setText((game.getCurrentPlayer() == PlayerType.CROSS) ? "X's Turn" : "O's Turn");
        } else if (game.getCurrentState() == GameState.DRAW) {
            statusBar.setText("It's a Draw! Click to restart.");
        } else if (game.getCurrentState() == GameState.CROSS_WON) {
            statusBar.setText("'X' Won! Click to restart.");
        } else if (game.getCurrentState() == GameState.NOUGHT_WON) {
            statusBar.setText("'O' Won! Click to restart.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TTTGraphics::new);
    }
}
