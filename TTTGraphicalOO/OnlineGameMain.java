import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class OnlineGameMain extends JPanel {

    private Board board;
    private GameStatus currentState;
    private GameStatus previousState;
    private Seed currentTurn;
    private JLabel statusBar;
    private GameDatabase db;
    private int gameId;
    private String playerName;
    private Seed myPlayerSeed;
    private Timer syncTimer;
    private Runnable onExit;
    private Image backgroundImage;
    private volatile boolean isSyncing = false;

    public OnlineGameMain(GameDatabase db, int gameId, String playerName, boolean isHost, Runnable onExit) {
        super(new BorderLayout());
        this.db = db;
        this.gameId = gameId;
        this.playerName = playerName;
        this.myPlayerSeed = isHost ? Seed.CROSS : Seed.NOUGHT;
        this.onExit = onExit;
        this.currentState = GameStatus.PLAYING;
        this.previousState = GameStatus.PLAYING;

        URL bgURL = getClass().getClassLoader().getResource("images/background.png");
        if (bgURL != null) {
            this.backgroundImage = new ImageIcon(bgURL).getImage();
        } else {
            System.err.println("Couldn't find background image: images/background.png");
        }

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JLabel playerInfoLabel = new JLabel(" You are: " + playerName + " (" + myPlayerSeed.getDisplayName() + ")");
        playerInfoLabel.setForeground(Color.WHITE);
        JButton exitButton = new JButton("Exit to Lobby");
        topPanel.add(playerInfoLabel, BorderLayout.WEST);
        topPanel.add(exitButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        board = new Board();
        board.setOpaque(false);
        add(board, BorderLayout.CENTER);

        statusBar = new JLabel("Loading game...", SwingConstants.CENTER);
        statusBar.setFont(new Font("Arial", Font.BOLD, 16));
        add(statusBar, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(400, 430));

        board.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handlePlayerMove(e.getPoint());
            }
        });
        exitButton.addActionListener(e -> exitGame());

        syncTimer = new Timer(2000, e -> syncWithDatabase());
        syncTimer.start();
    }

    private void handlePlayerMove(Point point) {
        if (currentState != GameStatus.PLAYING || myPlayerSeed != currentTurn) {
            return;
        }

        int row = point.y / board.getCellSize();
        int col = point.x / board.getCellSize();

        if (board.isValidMove(row, col)) {
            SoundEffect.EAT_FOOD.play();
            board.cells[row][col].content = myPlayerSeed;
            GameStatus localCheckState = board.stepGame(myPlayerSeed, row, col);

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    String winnerStatus = "playing";
                    if (localCheckState != GameStatus.PLAYING) {
                        winnerStatus = (localCheckState == GameStatus.CROSS_WON) ? "CROSS_WON" : (localCheckState == GameStatus.NOUGHT_WON) ? "NOUGHT_WON" : "DRAW";
                        db.setWinner(gameId, winnerStatus);
                    }
                    Seed nextTurn = (currentTurn == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                    db.updateGameState(gameId, board.serializeBoard(), nextTurn.toString());
                    return null;
                }
                @Override
                protected void done() {
                    syncWithDatabase();
                }
            }.execute();
        }
    }

    private void syncWithDatabase() {
        if (isSyncing) {
            return;
        }
        isSyncing = true;

        new SwingWorker<GameState, Void>() {
            @Override
            protected GameState doInBackground() {
                return db.fetchGameState(gameId);
            }

            @Override
            protected void done() {
                try {
                    GameState state = get();
                    if (state == null) {
                        JOptionPane.showMessageDialog(OnlineGameMain.this, "Error fetching game state. The game may have been terminated.");
                        exitGame();
                        return;
                    }

                    board.deserializeBoard(state.boardState);
                    currentTurn = state.currentTurn.equals("CROSS") ? Seed.CROSS : Seed.NOUGHT;

                    if (state.winner != null && !state.winner.equals("playing") && !state.winner.equals("waiting")) {
                        if (state.winner.contains("CROSS")) currentState = GameStatus.CROSS_WON;
                        else if (state.winner.contains("NOUGHT")) currentState = GameStatus.NOUGHT_WON;
                        else if (state.winner.equals("DRAW")) currentState = GameStatus.DRAW;
                    }

                    if (currentState != GameStatus.PLAYING && previousState == GameStatus.PLAYING) {
                        SoundEffect.DIE.play();
                    }
                    previousState = currentState;

                    updateStatusBar();
                    board.repaint();

                    if (currentState != GameStatus.PLAYING) {
                        syncTimer.stop();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                } finally {
                    isSyncing = false;
                }
            }
        }.execute();
    }

    private void updateStatusBar() {
        if (currentState == GameStatus.PLAYING) {
            statusBar.setText(currentTurn == myPlayerSeed ? "Your Turn (" + myPlayerSeed.getDisplayName() + ")" : "Waiting for opponent...");
        } else if (currentState == GameStatus.DRAW) {
            statusBar.setText("It's a Draw!");
        } else if (currentState == GameStatus.CROSS_WON) {
            statusBar.setText("'X' Won!");
        } else if (currentState == GameStatus.NOUGHT_WON) {
            statusBar.setText("'O' Won!");
        }
    }

    private void exitGame() {
        syncTimer.stop();
        onExit.run();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
