package be.khleuven.arnautsmichael.chip8;

/**
 *
 * @author Michaël Arnauts
 */
public interface IMemory {
    public byte getByte(int i);
    public void setByte(int i, byte b);

    public boolean loadRom(String fileDir, String fileName);
    public void closeRom();
    public boolean reloadRom();

    public String getRomName();
    public boolean isRomLoaded();
}
