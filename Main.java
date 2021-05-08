import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import src.*;

public class Main extends JFrame {
    private JPanel contentPanel = new JPanel();
    private String actualPath;
    private JTextArea actualPathField;
    private DisplayPanel currentSelected = null;
    private int position = 0;
    private Vector<String> destinations = new Vector<String>();
    private JButton backButton;
    private JButton forwardButton;

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        try {
            actualPath = new File("./").getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //destinations.setSize(10);

        contentPanel.setLayout(new GridBagLayout());
        JScrollPane contentPanelScroller = new JScrollPane(contentPanel);
        contentPanelScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        contentPanelScroller.setMaximumSize(new Dimension(800, 25));

	    setLocationRelativeTo(null);
	    setTitle("Game save name");
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new FlowLayout());
        setResizable(false);
        setSize(1280, 720);

        GridBagConstraints mainWindowPlacement = new GridBagConstraints();
        getContentPane().setLayout(new GridBagLayout());
        mainWindowPlacement.gridx = 0;
        mainWindowPlacement.gridy = 0;
        mainWindowPlacement.insets = new Insets(20, 0, 0, 0);

        GridBagConstraints controlPanelPlacement = new GridBagConstraints();
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanelPlacement.gridx = 0;
        controlPanelPlacement.gridy = 0;

        JPanel pathControl = new JPanel(new FlowLayout());
        controlPanel.add(pathControl, controlPanelPlacement);

        backButton = new JButton("<");
        backButton.addActionListener(new BackForwardButtonEvent(0));
        backButton.setEnabled(false);
        forwardButton = new JButton(">");
        forwardButton.addActionListener(new BackForwardButtonEvent(1));
        actualPathField = new JTextArea(actualPath);
        int actualPathFieldWidth = -30+(int)(getWidth()-backButton.getPreferredSize().getWidth()-forwardButton.getPreferredSize().getWidth());
        actualPathField.setPreferredSize(new Dimension(1000, 25));

        pathControl.add(backButton);
        pathControl.add(forwardButton);
        pathControl.add(actualPathField);

        controlPanelPlacement.gridy++;
        GridBagConstraints actionControlPlacement = new GridBagConstraints();
        JPanel actionControl = new JPanel(new GridBagLayout());
        controlPanel.add(actionControl, controlPanelPlacement);
        actionControlPlacement.gridx = 0;
        actionControlPlacement.gridy = 0;
        actionControlPlacement.insets = new Insets(0, 10, 0, 0);
        JButton smallSizeButton = new JButton("Petites icônes");
        JButton midSizeButton = new JButton("Moyennes icônes");
        JButton bigSizeButton = new JButton("Grandes icônes");
        JButton deleteButton = new JButton("Supprimer");
        JButton copyButton = new JButton("Copier");
        JButton pasteButton = new JButton("Coller");
        JButton renameButton = new JButton("Renommer");
        JButton listDisplayButton = new JButton("Affichage par liste");
        JButton tableDisplayButton = new JButton("Affichage par tableau");

        JButton[] actionButtons = {smallSizeButton, midSizeButton, bigSizeButton, deleteButton, copyButton, pasteButton, renameButton, listDisplayButton, tableDisplayButton};

        for (int i = 0; i < actionButtons.length; i++) {
            actionControl.add(actionButtons[i], actionControlPlacement);
            actionControlPlacement.gridx++;
        }

        getContentPane().add(controlPanel, mainWindowPlacement);

        mainWindowPlacement.gridy++;
        getContentPane().add(contentPanelScroller, mainWindowPlacement);
        updatePanel(actualPath);

        setVisible(true);
    }

    public class FolderClickEvent implements MouseListener {
        private String nextFolder;

        public FolderClickEvent(String nextFolder) {
            this.nextFolder = nextFolder;
        }

        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == 1 && e.getClickCount() >= 2) {                
                actualPath = actualPath + "/" + nextFolder;
                updatePanel(actualPath);
                destinations.add(actualPath);
                position++;
            }
        }

        public void mousePressed(MouseEvent e){}
        public void mouseEntered(MouseEvent e){}
        public void mouseExited(MouseEvent e){}
        public void mouseReleased(MouseEvent e){}
    }

    public class BackFolderEvent implements MouseListener {
        public void mouseClicked(MouseEvent e) {
            try {
                if (e.getButton() == 1 && e.getClickCount() >= 2) {                
                    actualPath = new File(actualPath).getParentFile().getCanonicalPath();
                    updatePanel(actualPath);
                    destinations.add(actualPath);
                }
            } catch(IOException err) {
                err.printStackTrace();
            }
        }

        public void mousePressed(MouseEvent e){}
        public void mouseEntered(MouseEvent e){}
        public void mouseExited(MouseEvent e){}
        public void mouseReleased(MouseEvent e){}
    }

    public class SelectedPanelDisplayer implements MouseListener {
        public void mouseClicked(MouseEvent e) {
            DisplayPanel f = (DisplayPanel)(e.getComponent());
            if (e.getButton() == 1 && e.getClickCount() >= 1) {
                if (currentSelected != f) {
                    if (currentSelected != null) {
                        currentSelected.displayNotSelected();
                    }
                    currentSelected = f;
                    f.displaySelected();
                } 
            }
        }

        public void mousePressed(MouseEvent e){}
        public void mouseEntered(MouseEvent e){}
        public void mouseExited(MouseEvent e){}
        public void mouseReleased(MouseEvent e){}
    }

    public class FileRightClickEvent implements MouseListener {
        private String nextFolder;

        public FileRightClickEvent(String nextFolder) {
            this.nextFolder = nextFolder;
        }

        public void mouseClicked(MouseEvent e) {
            DisplayPanel f = (DisplayPanel)(e.getComponent());
            f.displayNotSelected();
            if (e.getButton() == 1 && e.getClickCount() >= 1) {
                if (currentSelected != f) {
                    if (currentSelected != null) {
                        currentSelected.displayNotSelected();
                    }
                    currentSelected = f;
                    f.displaySelected();
                } 
            }
        }

        public void mousePressed(MouseEvent e){}
        public void mouseEntered(MouseEvent e){}
        public void mouseExited(MouseEvent e){}
        public void mouseReleased(MouseEvent e){}
    }

    public class BackForwardButtonEvent implements ActionListener {
        private int direction;

        public BackForwardButtonEvent(int direction) {
            this.direction = direction;
        }

        public void actionPerformed(ActionEvent e) {
            System.out.println(destinations.size());
            position = position + (direction == 0 ? -1 : +1);
            if (position == 0) {
                backButton.setEnabled(false);
            }
            if (position == 1 && direction == 1) {
                backButton.setEnabled(true);
            }
            if (destinations.size() == position-1) {
                forwardButton.setEnabled(false);
            }
            if (destinations.size() == position-2 ) {
                forwardButton.setEnabled(true);
            }
        }
    }

    public void updatePanel(String path) {
        contentPanel.removeAll();
        System.out.println(new File(actualPath).getParentFile());

        GridBagConstraints placement = new GridBagConstraints();
        placement.gridx = 0;
        placement.gridy = 0;
        placement.insets = new Insets(0, 15, 0, 0);

        if (new File(actualPath).getParentFile() != null) {
            DisplayPanel back = new DisplayPanel("../", true);
            back.addMouseListener(new BackFolderEvent());
            back.addMouseListener(new SelectedPanelDisplayer());
            contentPanel.add(back, placement);
            placement.gridx++;
        }

        File f = new File(path);
        String[] fileNames = f.list();
        if (fileNames.length != 0) {
            for (String fileName : fileNames) {
                boolean isFolder = new File(path + "/" + fileName).isDirectory();
                DisplayPanel temp = new DisplayPanel(fileName, isFolder);
                if (isFolder) {
                    temp.addMouseListener(new FolderClickEvent(fileName));
                } else {
                    temp.addMouseListener(new FileRightClickEvent(fileName));
                }
                temp.addMouseListener(new SelectedPanelDisplayer());
                contentPanel.add(temp, placement);
                placement.gridx++;
                if (placement.gridx % 7 == 0) {
                    placement.gridy++;
                    placement.gridx = 0;
                }
            }
        }
        contentPanel.setPreferredSize(new Dimension(-200+getWidth(), 550));
        actualPathField.setText(actualPath);
        validate();
        repaint();
    }
}