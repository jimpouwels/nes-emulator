package nesemulator;

import nesemulator.exception.MemoryAddressExceedsMemoryException;

public class Bus {

    private static final int MEMORY_RANGE = 0x1FFF;
    private int[] cpuRam;
    private int sizeInBytes;

    public Bus() {
        this.sizeInBytes = 2048;
        cpuRam = new int[sizeInBytes];
    }

    public void cpuWrite(int addr, int data) {
        if (addr >= 0x0000 && addr <= MEMORY_RANGE) {
            cpuRam[mapToRange(addr, sizeInBytes)] = data;
        } else {
            throw new MemoryAddressExceedsMemoryException("address " + addr + " exceeds memory size");
        }
    }

    public int cpuRead(int addr, boolean readOnly) {
        if (addr >= 0x0000 && addr <= MEMORY_RANGE) {
            return cpuRam[mapToRange(addr, sizeInBytes)];
        } else {
            throw new MemoryAddressExceedsMemoryException("address " + addr + " exceeds memory size");
        }
    }

    private int mapToRange(int addr, int targetRange) {
        return addr & (targetRange - 1);
    }
}
