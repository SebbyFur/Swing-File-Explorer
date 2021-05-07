import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import src.*;

public class Main extends JFrame {
    private JPanel content_window = new JPanel();
    private String actualPath;
    private static DisplayPanel currentSelected = null;

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        try {
            actualPath = new File("./").getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        content_window.setLayout(new GridBagLayout());

	    setLocationRelativeTo(null);
	    setTitle("Game save name");
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new FlowLayout());
        setResizable(false);
        setSize(1280, 720);

        getContentPane().setLayout(new GridBagLayout());

        JPanel controlPanel = new JPanel(new GridBagLayout());
        JPanel controlPanelFirstRow = new JPanel(new GridBagLayout());

        controlPanel.add(controlPanelFirstRow);

        GridBagConstraints test = new GridBagConstraints();
        test.gridx = 0;
        test.gridy = 0;
        controlPanelFirstRow.add(new JButton("Arrière"), test);

        test.gridx++;
        controlPanelFirstRow.add(new JButton("Avant"), test);
        test.gridx++;
        JTextArea e = new JTextArea(actualPath);
        e.setPreferredSize(new Dimension(1100, 25));
        controlPanelFirstRow.add(e, test);

        updatePanel(actualPath);

        JScrollPane scrollPane = new JScrollPane(content_window);
        scrollPane.setMaximumSize(new Dimension(getWidth(), 25));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        getContentPane().add(controlPanel);

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
            }
        }

        public void mousePressed(MouseEvent e){}
        public void mouseEntered(MouseEvent e){}
        public void mouseExited(MouseEvent e){}
        public void mouseReleased(MouseEvent e){}
    }

    public class BackClickEvent implements MouseListener {
        public void mouseClicked(MouseEvent e) {
            try {
                if (e.getButton() == 1 && e.getClickCount() >= 2) {                
                    actualPath = new File(actualPath).getParentFile().getCanonicalPath();
                    updatePanel(actualPath);
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

    public void updatePanel(String path) {
        content_window.removeAll();
        System.out.println(new File(actualPath).getParentFile());

        GridBagConstraints placement = new GridBagConstraints();
        placement.gridx = 0;
        placement.gridy = 0;

        if (new File(actualPath).getParentFile() != null) {
            DisplayPanel back = new DisplayPanel("../", true);
            back.addMouseListener(new BackClickEvent());
            back.addMouseListener(new SelectedPanelDisplayer());
            content_window.add(back, placement);
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
                content_window.add(temp, placement);
                placement.gridx++;
                if (placement.gridx % 8 == 0) {
                    placement.gridy++;
                    placement.gridx = 0;
                }
            }
        }
        content_window.setPreferredSize(new Dimension(1200, 800));
        validate();
        repaint();
    }

    public void addControlPanel(int posX, int posY) {
        JPanel tg = new JPanel();
        tg.setBounds(posX, posY, 200, 100);
        tg.add(new JLabel("tg"));
        content_window.add(tg);
    }
}