import java.io.File;
import java.nio.file.Files;
import javax.swing.*;
import java.awt.*;

public class Test extends JFrame {
    public static void main(String[] args) {
        new Test();
    }

    public Test() {
        setSize(1200,900);
	    setLocationRelativeTo(null);
	    setTitle("Game save name");
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new FlowLayout());
        File f = new File("./");

        String[] pathnames = f.list();

        for (String pathname : pathnames) {
            getContentPane().add(new JLabel(pathname));    
        }

        setVisible(true);
    }
}