package src;

import java.awt.*;
import javax.swing.*;
import java.io.File;

public class DisplayPanel extends JPanel {
    public DisplayPanel(String contentName, boolean isFolder) {
        super();
        this.setLayout(new BorderLayout());
        this.setMaximumSize(new Dimension(0, 0));
        this.add(isFolder ? new DisplayFolder() : new DisplayFile(), BorderLayout.PAGE_START);
        NextLineJLabel text = new NextLineJLabel(contentName);
        text.setHorizontalAlignment(JLabel.CENTER);
        this.add(text, BorderLayout.PAGE_END);
    }

    public void displaySelected() {
        this.setBorder(BorderFactory.createLineBorder(Color.black));
    }

    public void displayNotSelected() {
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        validate();
        repaint();
    }
}