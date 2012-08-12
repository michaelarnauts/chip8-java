package be.khleuven.arnautsmichael.chip8;

import java.awt.Dimension;
import java.awt.Point;

/**
 *
 * @author Michaël Arnauts
 */
public interface ILcd {
    public void cls();
    public void scdown(int n);
    public void scleft();
    public void scright();
    public void setHighMode(boolean mode);
    public byte sprite(Point point, int height, byte[] data);
    public boolean isHighMode();

    public void setScreenColor(ScreenColor screenColor);
    public ScreenColor getScreenColor();
}
