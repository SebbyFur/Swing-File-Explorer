/**
* Classe DisplayFolder. Utilisée par le constructeur dans DisplayPanel pour afficher des fichiers.
* @author DIOT Sébastien
* @version 10/05/2021
*/

package swinggui;

import java.awt.*;
import javax.swing.*;

public class DisplayFile extends JLabel {
    /**
    * Constructeur par initialisation pour afficher l'image du fichier.
    */
    public DisplayFile() {
        super();
        this.setHorizontalAlignment(JLabel.CENTER);
        Image img = new ImageIcon("textures/file.png").getImage().getScaledInstance(53, 53, Image.SCALE_DEFAULT);
        this.setIcon(new ImageIcon(img));
    }
}