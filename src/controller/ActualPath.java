/**
* Classe ActualPath, représente l'objet du chemin actuel que l'on serialize pour la communication entre chaque fenêtre.
* @author DIOT Sébastien
* @version 10/05/2021
*/

package controller;

public class ActualPath implements java.io.Serializable {
    /**
    * Chemin sous forme de String.
    */
    private String actualPath;

    /**
    * Constructeur par initialisation.
    * @param actualPath le chemin.
    */
    public ActualPath(String actualPath) {
        this.actualPath = actualPath;
    }

    /**
    * Getter du chemin
    * @return le chemin
    */
    public String getActualPath() {
        return actualPath;
    }
    
    /**
    * Setter du chemin
    * @param actualPath le chemin
    */
    public void setActualPath(String actualPath) {
        this.actualPath = actualPath;
    }
}