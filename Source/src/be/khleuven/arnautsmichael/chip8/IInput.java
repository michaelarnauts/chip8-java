package be.khleuven.arnautsmichael.chip8;

/**
 *
 * @author Michaël Arnauts
 */
public interface IInput {
    public void setKeyMapKeyboard(int[] keyMapKeyboard);
    public int[] getKeyMapKeyboard();
    public byte getKeyPressed();
    public boolean isKeyPressed();
    public boolean isKeyPressed(int k);
}
