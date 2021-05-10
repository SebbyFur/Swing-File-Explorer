package src.mainswing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.nio.file.StandardCopyOption;
import src.swinggui.*;
import src.controller.*;

public class MainSwing extends JFrame {
    private JPanel contentPanel = new JPanel();
    private JButton backButton;
    private JButton forwardButton;
    private Vector<String> destinations = new Vector<String>();
    private DisplayPanel currentSelected = null;
    private DisplayType mode = DisplayType.TABLE;
    private ActualPath actualPath;
    private JTextField actualPathField;
    private int position = 0;
    private File fileFakeClipBoard = null;

    public static void main(String[] args) {
        new MainSwing();
    }

    public MainSwing() {
        try {
            actualPath = new ActualPath(new File("./").getCanonicalPath());
            if (!(new File("./serializedPath.txt").exists())) {
                CustomSerializeObject.serialize(actualPath, "serializedPath.txt");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        destinations.add(actualPath.getActualPath());

        contentPanel.setLayout(new GridBagLayout());
        JScrollPane contentPanelScroller = new JScrollPane(contentPanel);
        contentPanelScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        contentPanelScroller.setPreferredSize(new Dimension(1000, 500));

	    setLocationRelativeTo(null);
	    setTitle("Explorateur de fichiers");
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
        backButton.addActionListener(new BackForwardButton(0));
        backButton.setEnabled(false);
        forwardButton = new JButton(">");
        forwardButton.addActionListener(new BackForwardButton(1));
        forwardButton.setEnabled(false);
        actualPathField = new JTextField(actualPath.getActualPath());
        int actualPathFieldWidth = -30+(int)(getWidth()-backButton.getPreferredSize().getWidth()-forwardButton.getPreferredSize().getWidth());
        actualPathField.setPreferredSize(new Dimension(1000, 25));
        actualPathField.addActionListener(new TextFieldNavigation());

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
        JButton deleteButton = new JButton("Supprimer");
        deleteButton.addActionListener(new DeleteButton());
        JButton copyButton = new JButton("Copier");
        copyButton.addActionListener(new CopyButton());
        JButton pasteButton = new JButton("Coller");
        pasteButton.addActionListener(new PasteButton());
        JButton listDisplayButton = new JButton("Affichage par liste");
        listDisplayButton.addActionListener(new ChangeModeButtons(DisplayType.LIST));
        JButton tableDisplayButton = new JButton("Affichage par tableau");
        tableDisplayButton.addActionListener(new ChangeModeButtons(DisplayType.TABLE));
        JButton reload = new JButton("Recharger [FX]");
        reload.addActionListener(new FXUpdateButton());

        JButton[] actionButtons = {deleteButton, copyButton, pasteButton, listDisplayButton, tableDisplayButton, reload};

        for (int i = 0; i < actionButtons.length; i++) {
            actionControl.add(actionButtons[i], actionControlPlacement);
            actionControlPlacement.gridx++;
        }

        getContentPane().add(controlPanel, mainWindowPlacement);

        mainWindowPlacement.gridy++;
        getContentPane().add(contentPanelScroller, mainWindowPlacement);
        updatePanel(actualPath.getActualPath());

        setVisible(true);
    }

    public class FolderClickEvent implements MouseListener {
        private String nextFolder;
        private int direction;

        public FolderClickEvent(String nextFolder, int direction) {
            this.nextFolder = nextFolder;
            this.direction = direction;
        }

        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == 1 && e.getClickCount() >= 2) {
                try {
                    if (direction == 0) {
                        actualPath.setActualPath(new File(actualPath.getActualPath()).getParentFile().getCanonicalPath());
                    } else {
                        actualPath.setActualPath(actualPath.getActualPath() + "/" + nextFolder);
                    }             
                    currentSelected = null;
                    updatePanel(actualPath.getActualPath());
                    position++;
                    for (int i = position; i < destinations.size(); i=i) {
                        destinations.remove(i);
                    }
                    destinations.add(actualPath.getActualPath());
                    backButton.setEnabled(true);
                    forwardButton.setEnabled(false);
                } catch(IOException err) {
                    err.printStackTrace();
                }
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

    public class BackForwardButton implements ActionListener {
        private int direction;

        public BackForwardButton(int direction) {
            this.direction = direction;
        }

        public void actionPerformed(ActionEvent e) {
            position = position + (direction == 0 ? -1 : +1);
            if (position == 0) {
                backButton.setEnabled(false);
            }
            if (position == 1 && direction == 1) {
                backButton.setEnabled(true);
            }
            if (destinations.size()-1 == position) {
                forwardButton.setEnabled(false);
            }
            if (destinations.size()-2 == position) {
                forwardButton.setEnabled(true);
            }
            updatePanel(destinations.get(position));
        }
    }

    public class TextFieldNavigation implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            File goTo = new File(actualPathField.getText());
            if (goTo.exists() && goTo.isDirectory()) {
                updatePanel(goTo.toString());
            } else {
                JOptionPane.showMessageDialog(MainSwing.this, "Le chemin spécifié est invalide !");
            }
        }
    }

    public class DeleteButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (currentSelected != null) {
                if (!((currentSelected.getContentName()).equals("../"))) {    
                    deleteFile(new File(actualPath.getActualPath() + "/" + currentSelected.getContentName()));
                    updatePanel(actualPath.getActualPath());
                } else {
                    JOptionPane.showMessageDialog(MainSwing.this, "Vous ne pouvez pas supprimer le dossier parent !");
                }
            } else {
                JOptionPane.showMessageDialog(MainSwing.this, "Veuillez sélectionner un fichier à supprimer !");
            }
        }
    }

    public class CopyButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (currentSelected != null) {
                File toCopy = new File((actualPath.getActualPath() + "/" + currentSelected.getContentName()));
                if (!(toCopy.isDirectory())) {
                    addFileToFakeClipboard(toCopy);
                } else {
                    JOptionPane.showMessageDialog(MainSwing.this, "Impossible de copier des dossiers pour le moment :( !");
                }
            } else {
                JOptionPane.showMessageDialog(MainSwing.this, "Veuillez sélectionner un fichier à copier !");
            }
        }
    }

    public class ChangeModeButtons implements ActionListener {
        private DisplayType type;

        public ChangeModeButtons(DisplayType type) {
            this.type = type;
        }

        public void actionPerformed(ActionEvent e) {
            mode = type;
            updatePanel(actualPath.getActualPath());
        }
    }

    public class PasteButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (fileFakeClipBoard != null) {
                pasteFile();
                updatePanel(actualPath.getActualPath());
            } else {
                JOptionPane.showMessageDialog(MainSwing.this, "Veuillez d'abord copier un fichier !");
            }
        }
    }

    public class FXUpdateButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String deserializedPath = (String)(CustomSerializeObject.deserialize("serializedPath.txt"));
            updatePanel(deserializedPath);
        }
    }

    public void updatePanel(String path) {
        contentPanel.removeAll();

        GridBagConstraints placement = new GridBagConstraints();
        placement.gridx = 0;
        placement.gridy = 0;
        placement.insets = new Insets(10, 15, 0, 0);
        if (mode == DisplayType.LIST) {
            placement.anchor = GridBagConstraints.WEST;
        }

        if (new File(path).getParentFile() != null) {
            DisplayPanel back = new DisplayPanel("../", true, mode);
            back.addMouseListener(new SelectedPanelDisplayer());
            back.addMouseListener(new FolderClickEvent(null, 0));
            contentPanel.add(back, placement);
            if (mode == DisplayType.TABLE) {
                placement.gridx++;
            } else {
                placement.gridy++;
            }
        }

        File f = new File(path);
        String[] fileNames = f.list();
        if (fileNames != null) {
            for (String fileName : fileNames) {
                boolean isFolder = new File(path + "/" + fileName).isDirectory();
                DisplayPanel temp = new DisplayPanel(fileName, isFolder, mode);
                temp.addMouseListener(new SelectedPanelDisplayer());
                if (isFolder) {
                    temp.addMouseListener(new FolderClickEvent(fileName, 1));
                }
                contentPanel.add(temp, placement);
                if (mode == DisplayType.TABLE) {
                    placement.gridx++;
                    if (placement.gridx % 7 == 0) {
                        placement.gridy++;
                        placement.gridx = 0;
                    }
                } else {
                    placement.gridy++;
                }
            }
        }
        actualPathField.setText(path);
        actualPath.setActualPath(path);
        CustomSerializeObject.serialize(actualPath.getActualPath(), "serializedPath.txt");
        validate();
        repaint();
    }

    public boolean deleteFile(File toDelete) {
        if (toDelete.isDirectory()) {
            for (File delNext : toDelete.listFiles()) {
                if (!(deleteFile(delNext))) {
                    JOptionPane.showMessageDialog(this, "Fichier/dossier non supprimé :(");
                    break;
                }
            }
        }
        return toDelete.delete();
    }

    public void addFileToFakeClipboard(File fileFakeClipBoard) {
        this.fileFakeClipBoard = fileFakeClipBoard;
    }

    public void pasteFile() {
        try {
            String fileName = fileFakeClipBoard.getName();
            Files.copy(fileFakeClipBoard.toPath(), new File(actualPath.getActualPath() + "/" + fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
            fileFakeClipBoard = null;
        } catch(IOException err) {
            err.printStackTrace();
        }
    }
}