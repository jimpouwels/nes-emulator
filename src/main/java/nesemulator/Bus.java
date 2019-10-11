package nesemulator;

import nesemulator.ppu.Olc2c02;

public class Bus {
    private static final int RAM_RANGE_START = 0x0000;
    private static final int RAM_RANGE_END = 0x1FFF;
    private static final int PPU_RANGE_START = 0x2000;
    private static final int PPU_RANGE_END = 0x3FFF;
    private Olc2c02 ppu;
    private final Ram ram;

    public Bus(Olc2c02 ppu) {
        this.ppu = ppu;
        this.ram = new Ram();
    }

    public void cpuWriteByte(int address_16, int data_8) {
        if (address_16 >= RAM_RANGE_START && address_16 <= RAM_RANGE_END) {
            ram.cpuWrite(address_16, data_8);
        } else if (address_16 >= PPU_RANGE_START && address_16 <= PPU_RANGE_END) {
            ppu.cpuWrite(address_16, data_8);
        }
    }

    public int cpuReadByte(int address_16, boolean readOnly) {
        if (address_16 >= RAM_RANGE_START && address_16 <= RAM_RANGE_END) {
            return ram.cpuRead(address_16);
        } else if (address_16 >= PPU_RANGE_START && address_16 <= PPU_RANGE_END) {
            ppu.cpuRead(address_16);
        }
        return 0x00;
    }

}
