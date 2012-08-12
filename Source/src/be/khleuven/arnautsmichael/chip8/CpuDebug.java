package be.khleuven.arnautsmichael.chip8;

/**
 *
 * @author Michaël Arnauts
 */
public class CpuDebug {
    
  /** Creates a new instance of CpuDebug */
    public CpuDebug() {
    }

    public String getOpcodeDescription(int opCode) {
        
        byte[] opCodeNibble = new byte[4];
        opCodeNibble[0] = (byte)((opCode & 0xF000) >> 12);
        opCodeNibble[1] = (byte)((opCode & 0x0F00) >> 8);
        opCodeNibble[2] = (byte)((opCode & 0x00F0) >> 4);
        opCodeNibble[3] = (byte)((opCode & 0x000F) >> 0);
        
        // Execute OpCode
        switch (opCodeNibble[0]) {
            case 0x0:
                switch (opCodeNibble[2]) {
                    case 0xC: // scdown
                        return "scdown " + opCodeNibble[3];
                    case 0x0E:
                        switch (opCodeNibble[3]) {
                            case 0x0: // cls
                                    return "cls";
                            case 0xE: // rts 
                                    return "rts";
                        }
                        break;
                    case 0xF:
                        switch (opCode & 0x000F) {
                            case 0xB: // scright
                                    return "scright";
                            case 0xC: // scleft 
                                    return "scleft";
                            case 0xD: // exit
                                    return "exit";
                            case 0xE: // low 
                                    return "low";
                            case 0xF: // high 
                                    return "high";
                    }
                    break;
                }
                break;
            case 0x1: // jmp xxx
                    return "jmp " + Integer.toHexString((short)(opCode & 0x0FFF) & 0x0FFF);
            case 0x2: // jsr xxx
                    return "jsr " + Integer.toHexString((short)(opCode & 0x0FFF) & 0x0FFF);
            case 0x3: // skeq vr,xx
                    return "skeq v" + opCodeNibble[1] + ", " + Integer.toHexString((byte)(opCode & 0x00FF) & 0x00FF);
            case 0x4: // skne vr,xx
                    return "skne v" + opCodeNibble[1] + ", " + Integer.toHexString((byte)(opCode & 0x00FF) & 0x00FF);
            case 0x5: // skeq vr,vy
                    return "skeq v" + opCodeNibble[1] + ", v" + opCodeNibble[2];
            case 0x6: // mov vr,xx
                    return "mov v" + opCodeNibble[1] + ", " + Integer.toHexString((byte)(opCode & 0x00FF) & 0x00FF);
            case 0x7: // add vr,xx
                    return "add v" + opCodeNibble[1] + ", " + Integer.toHexString((byte)(opCode & 0x00FF) & 0x00FF);
            case 0x8:
                switch (opCode & 0x000F) {
                    case 0x0: // mov vr,vy
                        return "mov v" + opCodeNibble[1] + ", v" + opCodeNibble[2];
                    case 0x1: // or rx,ry
                        return "or v" + opCodeNibble[1] + ", v" + opCodeNibble[2];
                    case 0x2: // and rx,ry
                        return "and v" + opCodeNibble[1] + ", v" + opCodeNibble[2];
                    case 0x3: // xor rx,ry
                        return "xor v" + opCodeNibble[1] + ", v" + opCodeNibble[2];
                    case 0x4: // add vr,vy
                        return "add v" + opCodeNibble[1] + ", v" + opCodeNibble[2];
                    case 0x5: // sub vr,vy
                        return "sub v" + opCodeNibble[1] + ", v" + opCodeNibble[2];
                    case 0x6: // shr vr
                        return "shr v" + opCodeNibble[1];
                    case 0x7: // rsb vr,vy
                        return "rsb v" + opCodeNibble[1] + ", v" + opCodeNibble[2];
                    case 0xE: // shl vr
                        return "shl v" + opCodeNibble[1];
                }
                break;
            case 0x9: // skne rx,ry
                return "skne v" + opCodeNibble[1] + ", v" + opCodeNibble[2];
            case 0xA: // mvi xxx
                return "mvi " + Integer.toHexString((short)(opCode & 0x0FFF) & 0x0FFF);
            case 0xB: // jmi xxx
                return "jmi " + Integer.toHexString((short)(opCode & 0x0FFF) & 0x0FFF);
            case 0xC: // rand vr,xx
                return "rand v" + opCodeNibble[1] + ", " + Integer.toHexString((opCode & 0x00FF) & 0x00FF);
            case 0xD: // sprite rx,ry,s
                if (opCodeNibble[3]==0)
                    return "xsprite v" + opCodeNibble[1] + ", v" + opCodeNibble[2];
                else
                    return "sprite v" + opCodeNibble[1] + ", v" + opCodeNibble[2] + ", " + opCodeNibble[3];
            case 0xE: 
                switch (opCode & 0x00FF) {
                    case 0x9E: // skpr k
                        return "skpr " + opCodeNibble[1];
                    case 0xA1: // skup k
                        return "skup " + opCodeNibble[1];
                }
                break;
            case 0xF:
                switch (opCode & 0x00FF) {
                    case 0x07: // gdelay vr
                        return "gdelay v" + opCodeNibble[1];
                    case 0x0A: // key vr
                        return "key v" + opCodeNibble[1];
                    case 0x15: // sdelay vr
                        return "sdelay v" + opCodeNibble[1];
                    case 0x18: // ssound vr
                        return "ssound v" + opCodeNibble[1];
                    case 0x1E: // adi vr
                        return "adi v" + opCodeNibble[1];
                    case 0x29: // font vr
                        return "font v" + opCodeNibble[1];
                    case 0x33: // bcd vr 
                        return "bcd v" + opCodeNibble[1];
                    case 0x55: // str v0-vr
                        return "stro v0-v" + opCodeNibble[1];
                    case 0x65: // ldr v0-vr
                        return "ldr v0-v" + opCodeNibble[1];
                }
        }
        return "Unknown";
    }
    
}
