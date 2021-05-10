package src.swinggui;

import java.awt.*;
import javax.swing.*;
import java.io.File;

public class DisplayPanel extends JPanel {
    private String contentName;

    public DisplayPanel(String contentName, boolean isFolder, DisplayType display) {
        super();
        this.contentName = contentName;
        this.setLayout(new GridBagLayout());
        GridBagConstraints placement = new GridBagConstraints();
        placement.gridx = 0;
        placement.gridy = 0;

        this.add(isFolder ? new DisplayFolder() : new DisplayFile(), placement);

        if (display == DisplayType.TABLE) {
            placement.gridy++;
            this.setPreferredSize(new Dimension(100, 80));
        } else {
            placement.gridx++;
        }

        JLabel text = new JLabel(contentName);
        text.setHorizontalAlignment(JLabel.CENTER);
        this.add(text, placement);
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