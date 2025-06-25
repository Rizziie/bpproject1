import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class OnlineLobby extends JPanel {

    private GameDatabase db;
    private TicTacToe.OnlineGameNavigator gameNavigator;
    private String playerName;
    private DefaultListModel<String> listModel;
    private JButton createBtn;
    private JButton refreshBtn;

    public OnlineLobby(GameDatabase db, TicTacToe.OnlineGameNavigator gameNavigator) {
        this.db = db;
        this.gameNavigator = gameNavigator;
        this.listModel = new DefaultListModel<>();

        setPreferredSize(new Dimension(400, 430));
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                promptForPlayerName();
                loadAvailableGames();
            }
        });

        JLabel titleLabel = new JLabel("Online Lobby", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        createBtn = new JButton("Host New Game");
        refreshBtn = new JButton("Refresh List");
        buttonPanel.add(createBtn);
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        JList<String> gameList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(gameList);
        add(scrollPane, BorderLayout.CENTER);

        createBtn.addActionListener(e -> {
            int gameId = db.createGame(playerName);
            if (gameId != -1) {
                JOptionPane.showMessageDialog(this, "Game created! Waiting for an opponent...");
                gameNavigator.navigateToGame(gameId, playerName, true);
            }
        });

        refreshBtn.addActionListener(e -> loadAvailableGames());

        gameList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = gameList.getSelectedValue();
                if (selected != null && selected.startsWith("Game ID:")) {
                    try {
                        int gameId = Integer.parseInt(selected.split(" ")[2]);
                        int confirm = JOptionPane.showConfirmDialog(this, "Join game " + gameId + "?", "Join Game", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            if (db.joinGame(gameId, playerName)) {
                                gameNavigator.navigateToGame(gameId, playerName, false);
                            } else {
                                JOptionPane.showMessageDialog(this, "Failed to join. Game might be full or no longer available.");
                                loadAvailableGames();
                            }
                        }
                    } catch (NumberFormatException ex) {
                        System.err.println("Could not parse game ID from: " + selected);
                    }
                }
            }
        });
    }

    private void promptForPlayerName() {
        if (playerName == null || playerName.trim().isEmpty()) {
            this.playerName = JOptionPane.showInputDialog(this, "Enter your name:", "Player Name", JOptionPane.PLAIN_MESSAGE);
            if (this.playerName == null || this.playerName.trim().isEmpty()) {
                this.playerName = "Player" + (int) (Math.random() * 1000);
            }
        }
    }

    private void loadAvailableGames() {
        listModel.clear();
        listModel.addElement("Loading available games...");
        createBtn.setEnabled(false);
        refreshBtn.setEnabled(false);

        new SwingWorker<List<Integer>, Void>() {
            @Override
            protected List<Integer> doInBackground() throws Exception {
                return db.getAvailableGames();
            }

            @Override
            protected void done() {
                try {
                    listModel.clear();
                    List<Integer> games = get();
                    if (games.isEmpty()) {
                        listModel.addElement("No available games. Host one!");
                    } else {
                        for (int id : games) {
                            listModel.addElement("Game ID: " + id);
                        }
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    listModel.clear();
                    listModel.addElement("Error loading games.");
                } finally {
                    createBtn.setEnabled(true);
                    refreshBtn.setEnabled(true);
                }
            }
        }.execute();
    }
}
