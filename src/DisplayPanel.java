package src;

import java.awt.*;
import javax.swing.*;
import java.io.File;

public class DisplayPanel extends JPanel {
    public DisplayPanel(String contentName, boolean isFolder) {
        super();
        this.setLayout(new BorderLayout());
        this.add(isFolder ? new DisplayFolder() : new DisplayFile());
        NextLineJLabel text = new NextLineJLabel(contentName);
        text.setHorizontalAlignment(JLabel.CENTER);
        this.add(text, BorderLayout.PAGE_END);
        this.setPreferredSize(new Dimension(300, 200));
    }

    public void displaySelected() {
        this.setBorder(BorderFactory.createLineBorder(Color.black));
    }

    public void displayNotSelected() {
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        repaint();
    }
}