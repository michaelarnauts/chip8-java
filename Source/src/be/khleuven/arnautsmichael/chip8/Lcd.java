package be.khleuven.arnautsmichael.chip8;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 *
 * @author Michaël Arnauts
 */
public class Lcd implements ImageProducer, ILcd {

    private boolean[][] screen;
    private int[] pixels;
   
    private final int lcdWidth = 128;
    private final int lcdHeight = 64;
    
    private boolean highMode = false;
    
    private ColorModel model = ColorModel.getRGBdefault();
    private Vector consumers = new Vector();

    private ScreenColor screenColor;
    
    /** Creates a new instance of lcd */
    public Lcd(ScreenColor screenColor) {
        this.screenColor = screenColor;
        
        screen = new boolean[lcdHeight][lcdWidth];
        pixels = new int[lcdHeight*lcdWidth];

        model = new DirectColorModel(32,0x00FF0000,0x000FF00,0x000000FF,0);;
        cls();
        setScreenColor(screenColor);
    }
    
    public void cls() {
        for (int i=0; i<lcdWidth; i++)
            for (int j=0; j<lcdHeight; j++)
                screen[j][i] = false;
        refreshImage();
    }
    
    public byte sprite(Point point, int height, byte[] data) {
        final boolean vwrap = false;
        final boolean hwrap = false;
        
        boolean collision = false;
        for (int i=0; i<height; i++) {
            boolean[] bits = getBits(data[i]);
            for (int j=0; j<8; j++) {
                if (bits[j]) // is needed?
                    if (highMode) {
                        if ((vwrap || point.y+i<lcdHeight) && (hwrap || point.x+j<lcdWidth)) {  // don't wrap
                            int x = ((point.x+j)%lcdWidth);
                            int y = ((point.y+i)%lcdHeight);
                            if (screen[y][x]) collision = true;
                            screen[y][x] = !screen[y][x];
                        }
                    } else {
                        if ((vwrap || point.y+i<lcdHeight/2) && (hwrap || point.x+j<lcdWidth/2)) {  // don't wrap
                            int x = ((point.x+j)%(lcdWidth/2));
                            int y = ((point.y+i)%(lcdHeight/2));
                            if (screen[y*2][x*2]) collision = true;
                            if (screen[y*2][x*2+1]) collision = true;
                            if (screen[y*2+1][x*2]) collision = true;
                            if (screen[y*2+1][x*2+1]) collision = true;
                            screen[y*2][x*2] = !screen[y*2][x*2];
                            screen[y*2][x*2+1] = !screen[y*2][x*2+1];
                            screen[y*2+1][x*2] = !screen[y*2+1][x*2];
                            screen[y*2+1][x*2+1] = !screen[y*2+1][x*2+1];
                        }
                    }
            }
        }
        refreshImage();
        return (byte)(collision? 1 : 0);
    }
    
    public void scdown(int n) {
        for (int i=0; i<lcdWidth; i++) {
            for (int j=lcdHeight-1; j>=0; j--) {
                if (j>=n)
                    screen[j][i] = screen[j-n][i];
                else
                    screen[j][i] = false;
            }
        }
        refreshImage();
    }
    
    public void scright() {
        for (int i=lcdWidth-1; i>=0; i--) {
            for (int j=0; j<lcdHeight; j++) {
               if (i>4)
                  screen[j][i] = screen[j][i-4];
               else
                  screen[j][i] = false;
            }
        }        
        refreshImage();
    }
    
    public void scleft() {
        for (int i=0; i<lcdWidth; i++) {
            for (int j=0; j<lcdHeight; j++) {
                if (i<lcdWidth-4)
                    screen[j][i] = screen[j][i+4];
                else
                    screen[j][i] = false;
            }
        }
        refreshImage();
    }
    
    public void setHighMode(boolean mode) {
        this.highMode = mode;
        refreshImage();
    }
    
    private boolean[] getBits(byte data) {
        boolean[] bits = new boolean[8];
        for(int i=0; i<8; i++)
           bits[i] = ((data & (0x80>>i)) > 0 ? true : false);
        return bits;
    }

    public void addConsumer(ImageConsumer ic) {
        if(!isConsumer(ic)) {
            consumers.addElement(ic);
   
            ic.setHints(ImageConsumer.TOPDOWNLEFTRIGHT | ImageConsumer.SINGLEPASS);
            ic.setDimensions(lcdWidth, lcdHeight);
            ic.setColorModel(model);
        }
    }

    public boolean isConsumer(ImageConsumer ic) {
        return consumers.contains(ic);
    }

    public void removeConsumer(ImageConsumer ic) {
        consumers.removeElement(ic);
    }

    public void startProduction(ImageConsumer ic) {
        addConsumer(ic);

        Enumeration e = consumers.elements();
        while(e.hasMoreElements()) {
            ImageConsumer icl = (ImageConsumer)e.nextElement();
            icl.setPixels(0, 0, lcdWidth, lcdHeight, model, pixels, 0, lcdWidth);
            icl.imageComplete(ImageConsumer.SINGLEFRAMEDONE);
        }   
    }

    public void requestTopDownLeftRightResend(ImageConsumer ic) { }
    
    public void setScreenColor(ScreenColor screenColor) {
        this.screenColor = screenColor;
        refreshImage();
    }
    
    public ScreenColor getScreenColor() { return screenColor; }
    
    private void refreshImage() {
        for (int i=0; i<lcdWidth; i++)
            for (int j=0; j<lcdHeight; j++) {
                if (screen[j][i])
                    pixels[j*lcdWidth + i] = screenColor.getForegroundColor().getRGB();
                else
                    pixels[j*lcdWidth + i] = screenColor.getBackgroundColor().getRGB();
            }
    }
    public boolean isHighMode() { return highMode; }

}
