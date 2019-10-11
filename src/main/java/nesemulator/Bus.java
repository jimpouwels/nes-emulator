package nesemulator;

import nesemulator.exception.MemoryAddressExceedsMemoryException;
import nesemulator.ppu.Olc2c02;

public class Bus {
    private static final int RAM_SIZE_IN_BYTES = 2048;
    private static final int RAM_RANGE_START = 0x0000;
    private static final int RAM_RANGE_END = 0x1FFF;
    private int[] cpuRam;
    private Olc2c02 ppu;

    public Bus(Olc2c02 ppu) {
        this.ppu = ppu;
        cpuRam = new int[RAM_SIZE_IN_BYTES];
    }

    public void cpuWriteByte(int address_16, int data_8) {
        if (address_16 >= RAM_RANGE_START && address_16 <= RAM_RANGE_END) {
            cpuRam[mapToInternalRange(address_16)] = data_8;
        } else if (ppu.isAddressInRange(address_16)) {
            ppu.cpuWrite(address_16, data_8);
        } else {
            throw new MemoryAddressExceedsMemoryException("address " + address_16 + " exceeds memory size");
        }
    }

    public int cpuReadByte(int address_16, boolean readOnly) {
        if (address_16 >= RAM_RANGE_START && address_16 <= RAM_RANGE_END) {
            return cpuRam[mapToInternalRange(address_16)];
        } else if (ppu.isAddressInRange(address_16)) {
            ppu.cpuRead(address_16);
        } else {
            throw new MemoryAddressExceedsMemoryException("address " + address_16 + " exceeds memory size");
        }
    }

    private int mapToInternalRange(int addr) {
        return addr & (RAM_SIZE_IN_BYTES - 1);
    }
}
