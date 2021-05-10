/**
* Classe MainSwing, classe principale pour l'interface graphique en Swing.
* @author DIOT Sébastien
* @version 10/05/2021
*/

package mainswing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.nio.file.StandardCopyOption;
import swinggui.*;
import controller.*;

public class MainSwing extends JFrame {
    /**
    * Panel contenant les DisplayPanel pour afficher les dossiers/fichiers
    */
    private JPanel contentPanel = new JPanel();

    /**
    * Bouton pour revenir en arrière
    */
    private JButton backButton;

    /**
    * Bouton pour aller en avant
    */
    private JButton forwardButton;

    /**
    * Vecteur contenant les destinations précédentes (utilisé pour les boutons back et forward)
    */
    private Vector<String> destinations = new Vector<String>();

    /**
    * DisplayPanel utilisé pour savoir quel dossier/fichier est actuellement sélectionné
    */
    private DisplayPanel currentSelected = null;

    /**
    * Type d'affichage (tableau ou liste)
    */
    private DisplayType mode = DisplayType.TABLE;

    /**
    * Objet sérialisé. Chemin actuel.
    */
    private ActualPath actualPath;

    /**
    * JTextField pour afficher le chemin actuel (et aussi pour le changer)
    */
    private JTextField actualPathField;

    /**
    * Position dans le vecteur destinations.
    */
    private int position = 0;

    /**
    * Faux presse-papier où l'on stocke la référence du fichier copié pour pouvoir le réutiliser dans la méthode paste.
    */
    private File fileFakeClipBoard = null;

    public static void main(String[] args) {
        new MainSwing();
    }
    
    /**
    * Constructeur par défaut
    */
    public MainSwing() {
        // Recherche du fichier serializedPath.txt. S'il n'exsite pas, on en créé un à partir du chemin existant.
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

    /**
    * Classe interne FolderClickEvent implémentant MouseListener. Utilisé lors d'un double clic sur un DisplayPanel.
    */
    public class FolderClickEvent implements MouseListener {
        /**
        * Fichier suivant sous forme de String.
        */
        private String nextFolder;

        /**
        * Direction. (0 pour arrière, 1 pour avant)
        */
        private int direction;

        
        /**
        * Constructeur par initialisation
        * @param nextFolder le fichier suivant sous forme de String
        * @param direction la direction (0 pour arrière, 1 pour avant)
        */
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

    /**
    * Classe interne SelectedPanelDisplayer implémentant MouseListener. Utilisée pour afficher les bordures
    * autour du DisplayPanel représentant la sélection actuelle.
    */
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

    /**
    * Classe interne BackForwardButton implémentant ActionListener. Utilisée pour les boutons back et forward.
    */
    public class BackForwardButton implements ActionListener {
        /**
        * Direction (0 pour arrière, 1 pour avant)
        */
        private int direction;

        /**
        * Constructeur par initialisation.
        * @param direction direction (0 pour arrière, 1 pour avant)
        */
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

    /**
    * Classe interne TextFieldNavigation implémentant ActionListener. Utilisé pour le JTextField actualPathField.
    */
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

    /**
    * Classe interne DeleteButton implémentant ActionListener. Utilisée pour le bouton supprimer.
    */
    public class DeleteButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (currentSelected != null) {
                if (!((currentSelected.getContentName()).equals("../"))) {    
                    deleteFile(new File(actualPath.getActualPath() + "/" + currentSelected.getContentName()));
                    currentSelected = null;
                    updatePanel(actualPath.getActualPath());
                } else {
                    JOptionPane.showMessageDialog(MainSwing.this, "Vous ne pouvez pas supprimer le dossier parent !");
                }
            } else {
                JOptionPane.showMessageDialog(MainSwing.this, "Veuillez sélectionner un fichier à supprimer !");
            }
        }
    }

    /**
    * Classe interne CopyButton implémentant ActionListener. Utilisée pour le bouton copier.
    */
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

    /**
    * Classe interne ChangeModeButton implémentant ActionListener. Utilisée pour les boutons d'affichage.
    */
    public class ChangeModeButtons implements ActionListener {
        /**
        * Type d'affichage (TABLE ou LIST).
        */
        private DisplayType type;

        /**
        * Constructeur par initialisation.
        * @param type type d'affichage (TABLE ou LIST).
        */
        public ChangeModeButtons(DisplayType type) {
            this.type = type;
        }

        public void actionPerformed(ActionEvent e) {
            mode = type;
            updatePanel(actualPath.getActualPath());
        }
    }

    /**
    * Classe interne PasteButton implémentant ActionListener. Utilisée pour le bouton coller.
    */
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

    /**
    * Classe interne FXUpdateButton implémentant ActionListener. Utilisée pour le bouton reload.
    */
    public class FXUpdateButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            ActualPath deserializedPath = (ActualPath)(CustomSerializeObject.deserialize("serializedPath.txt"));
            String deserializedPathName = deserializedPath.getActualPath();
            updatePanel(deserializedPathName);
        }
    }

    /**
    * Méthode updatePanel pour mettre à jour le JPanel contenant tous les DisplayPanels.
    * Un String est utilisé en tant que paramètre pour le nouveau chemin à afficher, mais dans la partie FX,
    * on utilise plutôt un File. Il n'y a aucune raison particulière à ce changement, je voulais juste voir
    * laquelle des deux solutions était la meilleure et je n'ai plus le temps de modifier le projet :(
    * @param path le nouveau chemin à afficher sous forme de String
    */
    public void updatePanel(String path) {
        contentPanel.removeAll();

        GridBagConstraints placement = new GridBagConstraints();
        placement.gridx = 0;
        placement.gridy = 0;
        placement.insets = new Insets(10, 15, 0, 0);
        if (mode == DisplayType.LIST) {
            placement.anchor = GridBagConstraints.WEST;
        }

        // Ici, si le fichier parent est null, on ne met pas de DisplayPanel de retour en arrière.
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
        // On liste tous les fichiers dans le répertoire. Si le répertoire est vide, rien n'est ajouté.
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
        CustomSerializeObject.serialize(actualPath, "serializedPath.txt");
        validate();
        repaint();
    }

    /**
    * Méthode récursive deleteFile.
    * @param toDelete fichier/dossier à supprimer
    * @return un booléen (pour savoir si oui ou non le fichier a été supprimé) 
    */
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

    /**
    * Méthode addFileToFakeClipboard. Utilisée pour ajouter un fichier au faux presse-papier.
    * @param fileFakeClipBoard le fichier à ajouter au faux presse-papier.
    */
    public void addFileToFakeClipboard(File fileFakeClipBoard) {
        this.fileFakeClipBoard = fileFakeClipBoard;
    }

    /**
    * Méthode pasteFile. Utilisée pour coller les fichiers.
    */
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