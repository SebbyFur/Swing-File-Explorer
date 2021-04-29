package src;

import java.awt.*;
import javax.swing.*;

public class NextLineJLabel extends JLabel {
    public NextLineJLabel(String text) {
        super();
        int i = 0;
        StringBuffer newText = new StringBuffer("");
        if (text.length() / 15 > 1) {
            newText.append("<html>");
            while (i < text.length() && i < 48) {
                newText.append(text.charAt(i));
                if (i%15 == 0 && i != 0) {
                    newText.append("<br/>");
                }
                i++;
            }
            newText.append("</html>");
        } else {
            newText.append(text);
        }
        this.setText(newText.toString());
    }
}