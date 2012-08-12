package be.khleuven.arnautsmichael.chip8;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 *
 * @author Michaël Arnauts
 */
public class Input extends KeyAdapter implements IInput {
      
    private boolean[] keys;
        
    private final byte keyMap[] = {
      0x01,0x02,0x03,0x0C,
      0x04,0x05,0x06,0x0D,
      0x07,0x08,0x09,0x0E,
      0x0A,0x00,0x0B,0x0F };

    // default keys
    private static int defaultKeyMapKeyboard[] = {
      KeyEvent.VK_1,KeyEvent.VK_2,KeyEvent.VK_3,KeyEvent.VK_4,
      KeyEvent.VK_A,KeyEvent.VK_Z,KeyEvent.VK_E,KeyEvent.VK_R,
      KeyEvent.VK_Q,KeyEvent.VK_S,KeyEvent.VK_D,KeyEvent.VK_F,
      KeyEvent.VK_W,KeyEvent.VK_X,KeyEvent.VK_C,KeyEvent.VK_V };
    
    private int keyMapKeyboard[];
    
    /** Creates a new instance of Input */
    public Input(int[] keyMapKeyboard) {
        keys = new boolean[16];
        for (int i=1; i<keys.length; i++)
            keys[i] = false;
        this.keyMapKeyboard = keyMapKeyboard;
    }
    
    public static int[] getDefaultKeyMapKeyboard() {
        return defaultKeyMapKeyboard.clone();
    }
    
    public int[] getKeyMapKeyboard() {
        return keyMapKeyboard;
    }
    
    public void setKeyMapKeyboard(int[] keyMapKeyboard) {
        this.keyMapKeyboard = keyMapKeyboard;
    }
        
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        for (int i=0; i<keys.length; i++)
            if (key == keyMapKeyboard[i]) {
                keys[keyMap[i]] = true;
                break;
            }
    } 

    public void keyReleased(KeyEvent e) {			
        int key = e.getKeyCode();
        for (int i=0; i<keys.length; i++)
            if (key == keyMapKeyboard[i]) {
                keys[keyMap[i]] = false;
                break;
            }
    }

    public boolean isKeyPressed(int k) {
        if ((k >= 0) && (k<keys.length))
            return keys[k];
        else {
            System.out.println("Invalid key: " + k);
            return false;
        }
    }        

    public boolean isKeyPressed() {
        for (int i=0; i<keys.length; i++)
            if (keys[i]) 
                return true;
        return false;
    }    

    public byte getKeyPressed() {
        for (byte i=0; i<keys.length; i++)
            if (keys[i]) {
//                keys[i] = false;
                return i;
            }
        return 0;
    }   
}
