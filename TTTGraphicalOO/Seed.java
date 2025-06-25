import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

public enum Seed {
    CROSS("X", "images/cross.gif", 'X'),
    NOUGHT("O", "images/not.gif", 'O'),
    NO_SEED(" ", null, '-');

    private final String displayName;
    private final char symbol;
    private Image img = null;

    Seed(String displayName, String imageFilename, char symbol) {
        this.displayName = displayName;
        this.symbol = symbol;

        if (imageFilename != null) {
            URL imgURL = getClass().getClassLoader().getResource(imageFilename);
            if (imgURL != null) {
                this.img = new ImageIcon(imgURL).getImage();
            } else {
                System.err.println("Couldn't find file: " + imageFilename);
            }
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public Image getImage() {
        return img;
    }

    public char getSymbol() {
        return symbol;
    }
}
