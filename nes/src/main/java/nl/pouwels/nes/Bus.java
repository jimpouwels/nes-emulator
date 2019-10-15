package nl.pouwels.nes;

import nl.pouwels.nes.cpu.Olc6502;
import nl.pouwels.nes.ppu.Olc2c02;

public class Bus {
    private static final int RAM_RANGE_START = 0x0000;
    private static final int RAM_RANGE_END = 0x1FFF;
    private static final int PPU_RANGE_START = 0x2000;
    private static final int PPU_RANGE_END = 0x3FFF;
    private final Ram ram;
    private int systemClockCounter;
    private Olc6502 cpu;
    private Olc2c02 ppu;
    private Cartridge cartridge;

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
        systemClockCounter = 0;
    }

    public void clock() {
        cpu.clock();
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
        }
    }

    public int cpuReadByte(int address_16, boolean readOnly) {
        if (cartridge.isInProgramRomRange(address_16)) {
            return cartridge.cpuReadByte(address_16);
        } else if (address_16 >= RAM_RANGE_START && address_16 <= RAM_RANGE_END) {
            return ram.cpuReadByte(address_16);
        } else if (address_16 >= PPU_RANGE_START && address_16 <= PPU_RANGE_END) {
            ppu.cpuRead(address_16);
        }
        return 0x00;
    }

}
