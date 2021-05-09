package src;

import java.awt.*;
import javax.swing.*;
import java.io.File;

public class DisplayPanel extends JPanel {
    private String contentName;

    public DisplayPanel(String contentName, boolean isFolder) {
        super();
        this.contentName = contentName;
        this.setLayout(new BorderLayout());
        this.add(isFolder ? new DisplayFolder() : new DisplayFile());
        JLabel text = new JLabel(contentName);
        text.setHorizontalAlignment(JLabel.CENTER);
        this.add(text, BorderLayout.PAGE_END);
        this.setPreferredSize(new Dimension(100, 80));
    }

    public String getContentName() {
        return contentName;
    }

    public void displaySelected() {
        this.setBorder(BorderFactory.createLineBorder(Color.black));
    }

    public void displayNotSelected() {
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        repaint();
    }
}