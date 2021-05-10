package src.mainfx;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.io.File;
import src.controller.*;

public class ControllerMainFX {
    private ActualPath actualPath;
    @FXML private Button bouton;
    @FXML private ListView list;

    public ControllerMainFX() {
        try {
            actualPath = new ActualPath(new File("./").getCanonicalPath());
            if (!(new File("./serializedPath.txt").exists())) {
                CustomSerializeObject.serialize(actualPath, "serializedPath.txt");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
        CustomSerializeObject.serialize(actualPath.getActualPath(), "serializedPath.txt");
    }

    public void swingUpdate() {
        String deserializedPath = (String)(CustomSerializeObject.deserialize("serializedPath.txt"));
        updatePanel(new File(deserializedPath));
    }
}