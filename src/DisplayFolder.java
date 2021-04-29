package src;

import java.awt.*;
import javax.swing.*;

public class DisplayFolder extends JLabel {
    private String contentName;

    public DisplayFolder() {
        super();
        this.setHorizontalAlignment(JLabel.CENTER);
        Image img = new ImageIcon("textures/folder.png").getImage().getScaledInstance(53, 53, Image.SCALE_DEFAULT);
        this.setIcon(new ImageIcon(img));
    }
}