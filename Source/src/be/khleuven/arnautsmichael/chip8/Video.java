package be.khleuven.arnautsmichael.chip8;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageProducer;
import javax.swing.JPanel;

/**
 *
 * @author Michaël Arnauts
 */
public class Video extends JPanel {
    private ImageProducer lcd;
    private Image screen;

    private int scale;
    private final int scaleFactor = 2;
    
    private final int lcdWidth = 128;
    private final int lcdHeight = 64;
    
    /** Creates a new instance of Video */
    public Video(ImageProducer lcd, int scale) {
        this.lcd = lcd;
        screen = createImage(lcd);
        setScale(scale);
    }

   public void paint(Graphics g) {
        screen.flush();
        g.drawImage(screen, 0, 0, lcdWidth*scale*scaleFactor, lcdHeight*scale*scaleFactor, null);
   }
   
    public Dimension getPreferredSize() {
        return new Dimension(lcdWidth*scale*scaleFactor, lcdHeight*scale*scaleFactor);
    }
    
    public int getScale() { return scale; }
    public void setScale(int s) { scale = s; }
}