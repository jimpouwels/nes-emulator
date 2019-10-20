package nl.pouwels.nes;

import nl.pouwels.nes.cartridge.Cartridge;
import nl.pouwels.nes.cpu.Olc6502;
import nl.pouwels.nes.ppu.Olc2c02;

public class Bus {
    private static final int RAM_RANGE_START = 0x0000;
    private static final int RAM_RANGE_END = 0x1FFF;
    private static final int PPU_RANGE_START = 0x2000;
    private static final int PPU_RANGE_END = 0x3FFF;

    public int[] controllers_8 = new int[2];
    private final Ram ram;
    private int systemClockCounter;
    private Olc6502 cpu;
    private Olc2c02 ppu;
    private Cartridge cartridge;
    private int[] controllersState_8 = new int[2];

    public Bus(Olc6502 cpu, Olc2c02 ppu) {
        this.cpu = cpu;
        this.ppu = ppu;
        this.ram = new Ram();
    }

    public void reset(int reset) {
        cpu.reset(reset);
        systemClockCounter = 0;
    }

    public void reset() {
        cpu.reset();
        ppu.reset();
        systemClockCounter = 0;
    }

    public void clock() {
        ppu.clock();
        if (systemClockCounter % 3 == 0) {
            cpu.clock();
        }
        if (ppu.nonMaskableInterrupt) {
            ppu.nonMaskableInterrupt = false;
            cpu.nmi();
        }
    }

    public void insertCartridge(Cartridge cartridge) {
        this.cartridge = cartridge;
        ppu.connectCartridge(cartridge);
    }

    public void cpuWriteByte(int address_16, int data_8) {
        if (cartridge.isInProgramRomRange(address_16)) {
            cartridge.cpuWriteByte(address_16, data_8);
        } else if (address_16 >= RAM_RANGE_START && address_16 <= RAM_RANGE_END) {
            ram.cpuWrite(address_16, data_8);
        } else if (address_16 >= PPU_RANGE_START && address_16 <= PPU_RANGE_END) {
            ppu.cpuWrite(address_16, data_8);
        } else if (address_16 >= 0x4016 && address_16 <= 0x4017) { // FIXME, controller input, extract method
            controllersState_8[address_16 & 0x01] = controllers_8[address_16 & 0x01];
        }
    }

    public int cpuReadByte(int address_16, boolean readOnly) {
        if (cartridge.isInProgramRomRange(address_16)) {
            return cartridge.cpuReadByte(address_16);
        } else if (address_16 >= RAM_RANGE_START && address_16 <= RAM_RANGE_END) {
            return ram.cpuReadByte(address_16);
        } else if (address_16 >= PPU_RANGE_START && address_16 <= PPU_RANGE_END) {
            return ppu.cpuReadByte(address_16);
        } else if (address_16 >= 0x4016 && address_16 <= 0x4017) { // FIXME, controller input, extract method
            int data = (controllersState_8[address_16 & 0x01] & 0x80) > 0 ? 1 : 0;
            controllersState_8[address_16 & 0x01] <<= 1; // shift to the left to get the next buttons state on the next read
            return data;
        }
        return 0x00;
    }

    public Olc2c02 getPpu() {
        return ppu;
    }

    public Olc6502 getCpu() {
        return cpu;
    }
}
