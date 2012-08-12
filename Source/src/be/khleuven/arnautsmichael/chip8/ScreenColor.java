package be.khleuven.arnautsmichael.chip8;

import java.awt.Color;

/**
 *
 * @author Michaël Arnauts
 */
public class ScreenColor {
    
    private Color foregroundColor;
    private Color backgroundColor;
    private String name;
    
    /** Creates a new instance of ScreenColor */
    public ScreenColor(Color foregroundColor, Color backgroundColor, String name) {
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
        this.name = name;
    }

    public Color getForegroundColor() { return foregroundColor; }
    public Color getBackgroundColor() { return backgroundColor; }
    public String getName() { return name; }
    public String toString() { return getName(); }
    public boolean equals(Object obj) { return (obj.toString() == this.toString()); }
    
}
