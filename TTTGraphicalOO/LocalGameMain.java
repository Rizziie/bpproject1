import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class LocalGameMain extends JPanel {

    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

    private Board board;
    private GameStatus currentState;
    private Seed currentPlayer;
    private JLabel statusBar;
    private GameDatabase db;
    private Image backgroundImage;

    public LocalGameMain(Runnable backToMenuCallback) {
        super(new BorderLayout());
        db = new GameDatabase();

        URL bgURL = getClass().getClassLoader().getResource("images/background.png");
        if (bgURL != null) {
            this.backgroundImage = new ImageIcon(bgURL).getImage();
        } else {
            System.err.println("Couldn't find background image: images/background.png");
        }

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setOpaque(false);
        JButton restartButton = new JButton("Restart");
        JButton quitButton = new JButton("Quit to Menu");
        topPanel.add(restartButton);
        topPanel.add(quitButton);
        add(topPanel, BorderLayout.NORTH);

        board = new Board();
        board.setOpaque(false);
        add(board, BorderLayout.CENTER);

        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_BG_STATUS);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(400, 30));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));
        add(statusBar, BorderLayout.PAGE_END);

        setPreferredSize(new Dimension(400, 430));

        board.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                handlePlayerMove(e.getPoint());
            }
        });
        restartButton.addActionListener(e -> newGame());
        quitButton.addActionListener(e -> backToMenuCallback.run());

        newGame();
    }

    private void handlePlayerMove(Point point) {
        if (currentState != GameStatus.PLAYING) {
            newGame();
            return;
        }

        int row = point.y / board.getCellSize();
        int col = point.x / board.getCellSize();

        if (board.isValidMove(row, col)) {
            SoundEffect.EAT_FOOD.play();
            currentState = board.stepGame(currentPlayer, row, col);

            if (currentState != GameStatus.PLAYING) {
                SoundEffect.DIE.play();
                if (currentState == GameStatus.CROSS_WON) db.saveWinner("X (Local)");
                else if (currentState == GameStatus.NOUGHT_WON) db.saveWinner("O (Local)");
            }

            currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
            board.repaint();
            updateStatusBar();
        }
    }

    public void newGame() {
        board.newGame();
        currentPlayer = Seed.CROSS;
        currentState = GameStatus.PLAYING;
        board.repaint();
        updateStatusBar();
    }

    private void updateStatusBar() {
        if (currentState == GameStatus.PLAYING) {
            statusBar.setForeground(Color.BLACK);
            statusBar.setText((currentPlayer == Seed.CROSS) ? "X's Turn" : "O's Turn");
        } else if (currentState == GameStatus.DRAW) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("It's a Draw! Click board or Restart.");
        } else if (currentState == GameStatus.CROSS_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'X' Won! Click board or Restart.");
        } else if (currentState == GameStatus.NOUGHT_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'O' Won! Click board or Restart.");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
