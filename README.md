Small file explorer written with Swing and FX in Java.

Pour lancer la partie Swing:
javac src/mainswing/*.java
java src.mainswing.MainSwing

Pour lancer la partie FX:
javac --module-path {chemin vers les libraires fx} --add-modules javafx.controls,javafx.fxml src/mainfx/*.java
java --module-path {chemin vers les libraires fx} --add-modules javafx.controls,javafx.fxml src.mainfx.MainFX