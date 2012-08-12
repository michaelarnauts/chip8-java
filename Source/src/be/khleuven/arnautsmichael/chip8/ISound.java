package be.khleuven.arnautsmichael.chip8;

/**
 *
 * @author Michaël Arnauts
 */
public interface ISound {
    public void startSound();    
    public void stopSound();
    
    public void setEnabled(boolean isEnabled);    

    public boolean isEnabled();
}
