/**
* Classe DisplayPanel. Etendue de JPanel, utilisée dans la méthode updatePanel de MainSwing pour afficher chaque
* élément dans le répertoire actuel.
* @author DIOT Sébastien
* @version 10/05/2021
*/

package swinggui;

import java.awt.*;
import javax.swing.*;
import java.io.File;

public class DisplayPanel extends JPanel {
    /**
    * Nom du fichier/dossier
    */
    private String contentName;

    /**
    * Constructeur par initialisation. L'objet se construit en fonction du type d'affichage choisi (display).
    * @param contentName le nom du fichier/dossier
    * @param isFolder booléen déterminant s'il s'agit d'un fichier ou d'un dossier.
    * @param display type d'affichage (TABLE ou LIST)
    */
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
    
    /**
    * Getter pour l'attribut contentName.
    * @return le nom du fichier/dossier.
    */
    public String getContentName() {
        return contentName;
    }

    /**
    * Méthode pour ajouter une bordure autour du panel. Utilisé pour la sélection.
    */
    public void displaySelected() {
        this.setBorder(BorderFactory.createLineBorder(Color.black));
    }

    /**
    * Méthode pour retirer la bordure autour du panel. Utilisé pour la sélection.
    */
    public void displayNotSelected() {
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        repaint();
    }
}