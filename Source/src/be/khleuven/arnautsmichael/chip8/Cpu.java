package be.khleuven.arnautsmichael.chip8;

import java.awt.Graphics;
import java.awt.Point;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Stack;
import javax.swing.JOptionPane;

/**
 *
 * @author Michaël Arnauts
 */
public class Cpu implements Runnable {
    private int nsToWait = 1;
    private int msToWait = 0;
    
    private Thread CPUThread;
    private boolean CPURunning = false;
    
    private Actie actie;
    
    private Lcd lcd;
    private Video video;
    private Memory memory;
    private Input input;
    private Sound sound;
    
    // Registers
    private int[] regV = new int[16];   // 16 Data registers (8 bit each)
    private int[] regHP48 = new int[8]; // 8 Data registers (8 bit each), HP48
    private int regI;                   // 1 Adress register (16 bit)

    // Timers (60Hz)
    private int timerDelay; // 8 bit
    private int timerSound;

    // Program Counter
    private int PC; // 16 bit
    
    // Current Opcode
    private int opCode;
    private int pOpCode; // previous
    
    // Stack (16 bit, 16 levels deep)
    private Stack<Integer> stack;
    
    /** Creates a new instance of cpu */
    public Cpu(Lcd lcd, Video video, Input input, Memory memory, Sound sound, Actie actie) {
        this.lcd = lcd;
        this.video = video;
        this.input = input;
        this.memory = memory;
        this.sound = sound;
        this.actie = actie;
        
        initCPU();
    }
    
    public int getReg(int i) { return regV[i]; }
    public int getRegI() { return regI; }
    public int getPC() { return PC; }
    public int getOpCode() { return opCode; }
    public int getPOpCode() { return pOpCode; }
    public int getTimerSound() { return timerSound; }
    public int getTimerDelay() { return timerDelay; }

    public void setReg(int i, int b) { regV[i] = b & 0xFF; }
    public void setRegI(int b) { regI = b & 0xFFFF; }
    public void setPC(int b) { PC = b & 0xFFFF; }
    
    public boolean getRunning() { return CPURunning; }

    public void start() {
        if (CPUThread == null) {
            CPURunning = true;
            CPUThread = new Thread(this);
            CPUThread.start();
        }
    }
    
    public void stop() {
        sound.stopSound();
        CPURunning = false;
        CPUThread = null;
    }
    
    public void run() {
        CPUThread.setPriority(Thread.NORM_PRIORITY);
        actie.actie();
        long startTime = System.currentTimeMillis();
        while (CPURunning) {
            if ((System.currentTimeMillis() - startTime) >= 16) { // 60 Hz = 1000/60
                startTime = System.currentTimeMillis();
                traceCPU(true); // decrease the timers
            } else {
                traceCPU(false); // don't decrease the timers
            }
            
            try {
                CPUThread.sleep(msToWait, nsToWait);
            } catch (InterruptedException ex) { ex.printStackTrace(); }
            
        }
        actie.actie();
    }
            
    public void initCPU() {
        timerDelay = 0;
        timerSound = 0;
        regI = 0;
        for (int i=0; i<0xF; i++)
            regV[i] = 0;
        for (int i=0; i<8; i++)
            regHP48[i] = 0;
        PC = 0x200;
        pOpCode = 0;
        opCode = 0;
        stack = new Stack();
        fetchOpcode();
        lcd.setHighMode(false);
        lcd.cls();
        video.repaint();
    }
       
    public void fetchOpcode() {
        pOpCode = opCode;
        opCode = ( (memory.getByte(PC)<<8) | (0x00FF & memory.getByte(PC+1)) );
    }

    public void traceCPU(boolean decreaseTimers) {
        int[] opCodeNibble = new int[4];
        opCodeNibble[0] = ((opCode & 0xF000) >> 12);
        opCodeNibble[1] = ((opCode & 0x0F00) >> 8);
        opCodeNibble[2] = ((opCode & 0x00F0) >> 4);
        opCodeNibble[3] = ((opCode & 0x000F) >> 0);
        
        if (timerSound > 0)
            sound.startSound();
        else
            sound.stopSound();
        
        // Lower the timers
        if (decreaseTimers) {
            if (timerDelay > 0) timerDelay--;
            if (timerSound > 0) timerSound--;
        }
        
        int temp;
        
        // Execute OpCode
        switch (opCodeNibble[0]) {
            case 0x0:
                switch (opCodeNibble[2]) {
                    case 0xC: // scdown
                        lcd.scdown(opCodeNibble[3]);
                        video.repaint();
                        setPC(PC+2);
                        break;
                    case 0x0E:
                        switch (opCodeNibble[3]) {
                            case 0x0: // cls
                                    lcd.cls();
                                    video.repaint();
                                    setPC(PC+2);
                                    break;
                            case 0xE: // rts 
                                    if (!stack.empty())
                                        setPC(stack.pop());
                                    else
                                        System.out.println("Stack is empty!");
                                    setPC(PC+2);
                                    break;
                        }
                        break;
                    case 0xF:
                        switch (opCode & 0x000F) {
                            case 0xB: // scright
                                    lcd.scright();
                                    video.repaint();
                                    setPC(PC+2);
                                    break;
                            case 0xC: // scleft 
                                    lcd.scleft();
                                    video.repaint();
                                    setPC(PC+2);
                                    break;
                            case 0xD: // exit
                                    stop();
                                    break;
                            case 0xE: // low 
                                    lcd.setHighMode(false);
                                    video.repaint();
                                    setPC(PC+2);
                                    break;
                            case 0xF: // high 
                                    lcd.setHighMode(true);
                                    video.repaint();
                                    setPC(PC+2);
                                    break;
                    }
                    break;
                }
                break;
            case 0x1: // jmp xxx
                    if (CPURunning && (PC == (opCode & 0x0FFF))) {
                        stop();
                    }
                    setPC(opCode & 0x0FFF);
                    break; 
            case 0x2: // jsr xxx
                    stack.push(PC);
                    setPC(opCode & 0x0FFF);
                    break; 
            case 0x3: // skeq vr,xx
                    if (getReg(opCodeNibble[1]) == (opCode & 0x00FF))
                        setPC(PC+4);
                    else
                        setPC(PC+2);
                    break;
            case 0x4: // skne vr,xx
                    if (getReg(opCodeNibble[1]) != (opCode & 0x00FF))
                        setPC(PC+4);
                    else
                        setPC(PC+2);
                    break;
            case 0x5: // skeq vr,vy
                    if (getReg(opCodeNibble[1]) == getReg(opCodeNibble[2]))
                        setPC(PC+4);
                    else
                        setPC(PC+2);
                    break;
            case 0x6: // mov vr,xx
                    setReg(opCodeNibble[1], (opCode & 0x00FF));
                    setPC(PC+2);
                    break;
            case 0x7: // add vr,xx
                    setReg(opCodeNibble[1], getReg(opCodeNibble[1]) + (opCode & 0x00FF));
                    setPC(PC+2);
                    break;
            case 0x8:
                switch (opCode & 0x000F) {
                    case 0x0: // mov vr,vy
                        setReg(opCodeNibble[1], getReg(opCodeNibble[2]));
                        setPC(PC+2);
                        break;
                    case 0x1: // or rx,ry
                        setReg(opCodeNibble[1], getReg(opCodeNibble[1]) | getReg(opCodeNibble[2]));
                        setPC(PC+2);
                        break;
                    case 0x2: // and rx,ry
                        setReg(opCodeNibble[1], getReg(opCodeNibble[1]) & getReg(opCodeNibble[2]));
                        setPC(PC+2);
                        break;
                    case 0x3: // xor rx,ry
                        setReg(opCodeNibble[1], getReg(opCodeNibble[1]) ^ getReg(opCodeNibble[2]));
                        setPC(PC+2);
                        break;
                    case 0x4: // add vr,vy
                        temp = getReg(opCodeNibble[1]) + getReg(opCodeNibble[2]);
                        setReg(opCodeNibble[1], temp);
                        setReg(0xF, (temp & 0x0F00) >> 8);
                        setPC(PC+2);
                        break;
                    case 0x5: // sub vr,vy
                        temp = getReg(opCodeNibble[2]) <= getReg(opCodeNibble[1])? 0x01 : 0x00;
                        setReg(opCodeNibble[1], getReg(opCodeNibble[1]) - getReg(opCodeNibble[2]));
                        setReg(0xF, temp);
                        setPC(PC+2);
                        break;
                    case 0x6: // shr vr
                        temp = (getReg(opCodeNibble[1])& 1);
                        setReg(opCodeNibble[1], getReg(opCodeNibble[1]) >> 1);
                        setReg(0xF, temp);
                        setPC(PC+2);
                        break;
                    case 0x7: // rsb vr,vy
                        temp = ((getReg(opCodeNibble[1]) <= getReg(opCodeNibble[2]))? 0x01 : 0x00);
                        setReg(opCodeNibble[1], getReg(opCodeNibble[2]) - getReg(opCodeNibble[1]));
                        setReg(0xF, temp);
                        setPC(PC+2);
                        break;
                    case 0xE: // shl vr
                        temp = ((getReg(opCodeNibble[1])>>7) > 0)? 0x01 : 0x00;
                        setReg(opCodeNibble[1], (getReg(opCodeNibble[1])<<1) & 0x0FFF);
                        setReg(0xF, temp);
                        setPC(PC+2);
                        break;
                }
                break;
            case 0x9: // skne rx,ry
                if (getReg(opCodeNibble[1]) != getReg(opCodeNibble[2]))
                    setPC(PC+4);
                else
                    setPC(PC+2);
                break;
            case 0xA: // mvi xxx
                setRegI(opCode & 0x0FFF);
                setPC(PC+2);
                break;
            case 0xB: // jmi xxx
                setPC(((opCode & 0x0FFF) + getReg(0)) & 0x0FFF);
                break;
            case 0xC: // rand vr,xxx
                setReg(opCodeNibble[1], (int)(Math.random()*0xFF) & (opCode & 0x00FF));
                setPC(PC+2);
                break;
            case 0xD: // sprite rx,ry,s
                if (opCodeNibble[3] == 0) { // 16 x 16 pixels
                    if (lcd.isHighMode()) {
                        byte[] spriteL = new byte[16];
                        byte[] spriteR = new byte[16];
                        for (int i=0; i<16; i++) {
                            spriteL[i] = memory.getByte(regI + i*2);
                            spriteR[i] = memory.getByte(regI + i*2 + 1);
                        }
                        temp = lcd.sprite(new Point(regV[opCodeNibble[1]]&0xFF, regV[opCodeNibble[2]]&0xFF), 16, spriteL);
                        temp |= lcd.sprite(new Point((regV[opCodeNibble[1]]+8)&0xFF, regV[opCodeNibble[2]]&0xFF), 16, spriteR);
                        setReg(0xF, temp);
                    } else { // 8 x 16 pixels
                        byte[] sprite = new byte[16];
                        for (int i=0; i<16; i++) 
                            sprite[i] = memory.getByte(regI + i);
                        setReg(0xF, lcd.sprite(new Point(regV[opCodeNibble[1]]&0xFF, regV[opCodeNibble[2]]&0xFF), 16, sprite));
                    }
                } else { // 8 x opCodeNibble[3] pixels
                    byte[] sprite = new byte[opCodeNibble[3]];
                    for (int i=0; i<opCodeNibble[3]; i++) 
                        sprite[i] = memory.getByte(regI + i);
                    setReg(0xF, lcd.sprite(new Point(regV[opCodeNibble[1]]&0xFF, regV[opCodeNibble[2]]&0xFF), opCodeNibble[3], sprite));
                }
                
                video.repaint();
                setPC(PC+2);
                break;
            case 0xE: 
                switch (opCode & 0x00FF) {
                    case 0x9E: // skpr k
                        CPUThread.yield();
                        if (input.isKeyPressed(getReg(opCodeNibble[1])))
                            setPC(PC+4);
                        else
                            setPC(PC+2);
                        break;
                    case 0xA1: // skup k
                        CPUThread.yield();
                        if (!input.isKeyPressed(getReg(opCodeNibble[1])))
                            setPC(PC+4);
                        else
                            setPC(PC+2);
                        break;
                }
                break;
            case 0xF:
                switch (opCode & 0x00FF) {
                    case 0x07: // gdelay vr
                        setReg(opCodeNibble[1], timerDelay);
                        setPC(PC+2);
                        break;
                    case 0x0A: // key vr
                        CPUThread.yield();
                        if (input.isKeyPressed()) {
                            setReg(opCodeNibble[1], input.getKeyPressed());
                            setPC(PC+2);
                        }
                        break;
                    case 0x15: // sdelay vr
                        timerDelay = getReg(opCodeNibble[1]) & 0x00FF;
                        setPC(PC+2);
                        break;
                    case 0x18: // ssound vr
                        timerSound = getReg(opCodeNibble[1]) & 0x00FF;
                        setPC(PC+2);
                        break;
                    case 0x1E: // adi vr
                        setRegI(getRegI() + (getReg(opCodeNibble[1]) & 0x00FF));
                        setPC(PC+2);
                        break;
                    case 0x29: // font vr
                        setRegI(getReg(opCodeNibble[1]) * 5);
                        setPC(PC+2);
                        break;
                    case 0x30: // xfont vr
                        setRegI(0x50 + getReg(opCodeNibble[1]) * 10);
                        setPC(PC+2);
                        break;
                    case 0x33: // bcd vr 
                        memory.setByte(getRegI(), (byte)(getReg(opCodeNibble[1])/100));
			memory.setByte(getRegI()+1, (byte)((getReg(opCodeNibble[1])%100)/10));
			memory.setByte(getRegI()+2, (byte)(getReg(opCodeNibble[1])%10));
                        setPC(PC+2);
                        break;
                    case 0x55: // str v0-vr
                        for (short i=0; i<=opCodeNibble[1]; i++)
                            memory.setByte(getRegI()+i, (byte)getReg(i));
//                        regI += opCodeNibble[1] + 1;
                        setPC(PC+2);
                        break;
                    case 0x65: // ldr v0-vr
                        for (short i=0; i<=opCodeNibble[1]; i++)
                            setReg(i, memory.getByte(getRegI() + i));
//                        regI += opCodeNibble[1] + 1;
                        setPC(PC+2);
                        break;
                    case 0x75: // strhp48 v0-vr
                        temp = (opCodeNibble[1] < 8 ? opCodeNibble[1] : 7);
                        for (short i=0; i<=temp; i++)
                            regHP48[i] = getReg(i);
                        setPC(PC+2);
                        break;
                    case 0x85: // ldrhp58 v0-vr
                        temp = (opCodeNibble[1] < 8 ? opCodeNibble[1] : 7);
                        for (short i=0; i<=temp; i++)
                            setReg(i, regHP48[i]);
                        setPC(PC+2);
                        break;
                }
        }
        // Fetch next opcode
        fetchOpcode();
    }
}
