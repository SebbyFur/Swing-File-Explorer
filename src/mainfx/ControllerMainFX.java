/**
* Classe ControllerMainFX, classe contrôleur de l'interface MainFX.
* @author DIOT Sébastien
* @version 10/05/2021
*/

package mainfx;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.io.File;
import controller.*;

public class ControllerMainFX {
    /**
    * Objet sérialisé. Chemin actuel.
    */
    private ActualPath actualPath;

    /**
    * ListView pour l'affichage du répertoire.
    */
    @FXML private ListView list;

    /**
    * Constructeur par initialisation.
    */
    public ControllerMainFX() {
        // Recherche du fichier serializedPath.txt. S'il n'exsite pas, on en créé un à partir du chemin existant.
        try {
            actualPath = new ActualPath(new File("./").getCanonicalPath());
            if (!(new File("./serializedPath.txt").exists())) {
                CustomSerializeObject.serialize(actualPath, "serializedPath.txt");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
    * Méthode initialize. Initialise la fenêtre, mets à jour la ListView avec la méthode updatePanel
    * Ajoute un MouseListener sur la ListView pour récupérer le nom du fichier/dossier sélectionné.
    */
    public void initialize() {
        updatePanel(new File(actualPath.getActualPath()));
        list.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    String selectedItem = list.getSelectionModel().getSelectedItem().toString();
                    File file = null;
                    if (selectedItem.equals("../")) {
                        try {
                            file = new File(actualPath.getActualPath()).getCanonicalFile().getParentFile();
                        } catch(IOException err) {
                            err.printStackTrace();
                        }
                    } else {
                        try {
                            file = new File(actualPath.getActualPath() + "/" + selectedItem).getCanonicalFile();
                        } catch(IOException err) {
                            err.printStackTrace();
                        }
                    }
                    if (file.isDirectory()) {
                        updatePanel(file);
                    }
                }
            }
        });
    }

    /**
    * Méthode updatePanel. Aussi utilisée dans Swing, mais avec un String et non un File.
    * @param file nom du répertoire à afficher.
    */
    public void updatePanel(File file) {
        list.getItems().clear();
        File parent = file.getParentFile();
        String newPath = file.toString();

        String[] fileNames = file.list();

        if (parent != null) {
            list.getItems().add("../");
        }

        if (fileNames != null) {
            for (String fileName : fileNames) {
                list.getItems().add(fileName);
            }
        }

        actualPath.setActualPath(newPath);
        CustomSerializeObject.serialize(actualPath, "serializedPath.txt");
    }

    /**
    * Méthode swingUpdate. Utilisé dans le MouseListener de la ListView.
    */
    public void swingUpdate() {
        ActualPath deserializedPath = (ActualPath)(CustomSerializeObject.deserialize("serializedPath.txt"));
        String deserializedPathName = deserializedPath.getActualPath();
        updatePanel(new File(deserializedPathName));
    }
}