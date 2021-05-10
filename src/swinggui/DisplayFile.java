package src.swinggui;

import java.awt.*;
import javax.swing.*;

public class DisplayFile extends JLabel {
    public DisplayFile() {
        super();
        this.setHorizontalAlignment(JLabel.CENTER);
        Image img = new ImageIcon("textures/file.png").getImage().getScaledInstance(53, 53, Image.SCALE_DEFAULT);
        this.setIcon(new ImageIcon(img));
    }
}