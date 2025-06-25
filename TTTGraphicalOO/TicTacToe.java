import javax.swing.*;
import java.awt.*;

public class TicTacToe {

    @FunctionalInterface
    public interface OnlineGameNavigator {
        void navigateToGame(int gameId, String playerName, boolean isHost);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Tic-Tac-Toe Among Us");
            CardLayout cardLayout = new CardLayout();
            JPanel mainPanel = new JPanel(cardLayout);

            GameDatabase db = new GameDatabase();

            Runnable showMenu = () -> cardLayout.show(mainPanel, "MENU");
            Runnable showLocalGame = () -> cardLayout.show(mainPanel, "LOCAL_GAME");
            Runnable showOnlineLobby = () -> cardLayout.show(mainPanel, "ONLINE_LOBBY");

            OnlineGameNavigator showOnlineGame = (gameId, playerName, isHost) -> {
                OnlineGameMain onlineGamePanel = new OnlineGameMain(db, gameId, playerName, isHost, showOnlineLobby);
                String panelName = "ONLINE_GAME_" + gameId;
                mainPanel.add(onlineGamePanel, panelName);
                cardLayout.show(mainPanel, panelName);
            };

            MenuPanel menuPanel = new MenuPanel(showLocalGame, showOnlineLobby);
            LocalGameMain localGamePanel = new LocalGameMain(showMenu);
            OnlineLobby onlineLobbyPanel = new OnlineLobby(db, showOnlineGame);

            mainPanel.add(menuPanel, "MENU");
            mainPanel.add(localGamePanel, "LOCAL_GAME");
            mainPanel.add(onlineLobbyPanel, "ONLINE_LOBBY");

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(mainPanel);
            frame.setResizable(false);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    db.close();
                }
            });
        });
    }
}
