package src.swinggui;

import java.awt.*;

public enum DisplayType {
    TABLE(BorderLayout.PAGE_END),
    LIST(BorderLayout.LINE_END);

    public String position;
    
    private DisplayType(String position) {
        this.position = position;
    }
}