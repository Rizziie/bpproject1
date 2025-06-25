// File: MenuPanel.java

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class MenuPanel extends JPanel {

    private Image backgroundImage;

    /**
     * Creates the main menu panel.
     * @param startLocalGameCallback A function to run when the "Play Local" button is clicked.
     * @param startOnlineGameCallback A function to run when the "Play Online" button is clicked.
     */
    public MenuPanel(Runnable startLocalGameCallback, Runnable startOnlineGameCallback) {
        setPreferredSize(new Dimension(400, 430));
        setLayout(new BorderLayout());

        // --- Background Image ---
        // FIX: This loads the image from a resource folder (e.g., src/images)
        // This is the correct way to load resources in a Java project.
        URL bgURL = getClass().getClassLoader().getResource("images/background.png");
        if (bgURL != null) {
            this.backgroundImage = new ImageIcon(bgURL).getImage();
        } else {
            // This error message is crucial for debugging if the image is not found.
            System.err.println("Couldn't find background image: images/background.png");
        }


        // --- Title Label ---
        JLabel titleLabel = new JLabel("Among Us Tic-Tac-Toe", JLabel.CENTER);
        titleLabel.setFont(new Font("OCR A Extended", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        // Use an empty border to push the title down from the top edge.
        titleLabel.setBorder(BorderFactory.createEmptyBorder(60, 0, 0, 0));
        add(titleLabel, BorderLayout.NORTH);


        // --- Buttons Panel ---
        // Use GridBagLayout for more control over button positioning.
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false); // Make the panel transparent to see the background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Each component on a new line
        gbc.insets = new Insets(10, 10, 10, 10);     // Add padding around buttons

        // Local Game Button
        JButton localButton = new JButton("Play Local");
        localButton.setFont(new Font("OCR A Extended", Font.PLAIN, 18));
        localButton.addActionListener(e -> {
            SoundEffect.MENU_CLICK.play();
            startLocalGameCallback.run();
        });

        // Online Game Button
        JButton onlineButton = new JButton("Play Online");
        onlineButton.setFont(new Font("OCR A Extended", Font.PLAIN, 18));
        onlineButton.addActionListener(e -> {
            SoundEffect.MENU_CLICK.play();
            startOnlineGameCallback.run();
        });

        buttonPanel.add(localButton, gbc); // Add button with constraints
        buttonPanel.add(onlineButton, gbc); // Add button with constraints

        add(buttonPanel, BorderLayout.CENTER);
    }

    /**
     * Overridden to draw the background image behind all other components.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the background image, stretching it to fill the entire panel.
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}