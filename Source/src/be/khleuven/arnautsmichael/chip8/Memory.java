package be.khleuven.arnautsmichael.chip8;

import java.io.FileInputStream;
import java.io.IOException;

/**
 *
 * @author Michaël Arnauts
 */
public class Memory implements IMemory {
    
    // 4KB Memory
    private byte[] memory = new byte[0x1000];
    private String fileDir;
    private String fileName;
    private boolean romLoaded;
    
    /** Creates a new instance of Memory */
    public Memory() {
        closeRom(); // clear the memory, load the fonts
    }

    public byte getByte(int i) {
        if (i >= 0 && i < memory.length)
            return memory[i];
        else {
            System.out.println("Access beyond memory limit!");
            return 0;
        }
    }
    
    public void setByte(int i, byte b) {
        if (i >= 0 && i < memory.length)
            memory[i] = b;
        else
            System.out.println("Access beyond memory limit!");
    }
    
    private void loadFonts() {
        // Small fonts for Chip8
        final char[] font = {
            0xf9,0x99,0xf2,0x62,0x27,
            0xf1,0xf8,0xff,0x1f,0x1f,
            0x99,0xf1,0x1f,0x8f,0x1f,
            0xf8,0xf9,0xff,0x12,0x44,
            0xf9,0xf9,0xff,0x9f,0x1f,
            0xf9,0xf9,0x9e,0x9e,0x9e,
            0xf8,0x88,0xfe,0x99,0x9e,
            0xf8,0xf8,0xff,0x8f,0x88 };

        for (short i=0; i<40; i++) {
            setByte((short)(i*2),   (byte)((font[i]&0xf0))); // First hex
            setByte((short)(i*2+1), (byte)(font[i]<<4));     // Second hex
	}
        
        // Large fonts for SuperChip8
        final char[] SuperFont = {
            0x3C,0x7E, 0xE7,0xC3, 0xC3,0xC3, 0xC3,0xE7, 0x7E,0x3C,
            0x18,0x38, 0x58,0x18, 0x18,0x18, 0x18,0x18, 0x18,0x3C,
            0x3E,0x7F, 0xC3,0x06, 0x0C,0x18, 0x30,0x60, 0xFF,0xFF,
            0x3C,0x7E, 0xC3,0x03, 0x0E,0x0E, 0x03,0xC3, 0x7E,0x3C,
            0x06,0x0E, 0x1E,0x36, 0x66,0xC6, 0xFF,0xFF, 0x06,0x06,
            0xFF,0xFF, 0xC0,0xC0, 0xFC,0xFE, 0x03,0xC3, 0x7E,0x3C,
            0x3E,0x7C, 0xC0,0xC0, 0xFC,0xFE, 0xC3,0xC3, 0x7E,0x3C,
            0xFF,0xFF, 0x03,0x06, 0x0C,0x18, 0x30,0x60, 0x60,0x60,
            0x3C,0x7E, 0xC3,0xC3, 0x7E,0x7E, 0xC3,0xC3, 0x7E,0x3C,
            0x3C,0x7E, 0xC3,0xC3, 0x7F,0x3F, 0x03,0x03, 0x3E,0x7C };
        
        for(int i=0; i<100; i++) {
            setByte(0x50+i, (byte)SuperFont[i]);
        }
    }
    
    public boolean loadRom(String fileDir, String fileName) {
        romLoaded = false;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(fileDir + fileName);
            this.fileDir = fileDir;
            this.fileName = fileName;
            
            int offset = 0x200;
            int nextByte;
            while ((nextByte = fileInputStream.read()) != -1) {
                if (offset < memory.length)
                    setByte(offset++, (byte)nextByte);
                else {
                    fileInputStream.close();
                    return false;
                }
            }            
            romLoaded = true;
            fileInputStream.close();
            return true;
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }
    }
    
    public boolean reloadRom() {
//        closeRom();
        return loadRom(fileDir, fileName);
    }
    
    public void closeRom() { 
        fileDir = "";
        fileName = "";
        romLoaded = false;
//        for (int i=0; i<memory.length; i++)
//            memory[i] = 0;
        loadFonts();
    }
    
    public boolean isRomLoaded() { return romLoaded; }
    public String getRomName() { return fileName; }
    
}
