package nesemulator;

import nesemulator.exception.MemoryAddressExceedsMemoryException;

public class Bus {

    private int[] ram;
    private long sizeInBytes;

    public Bus(int sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
        ram = new int[sizeInBytes];
    }

    public void write(int addr, int data) {
        if (addr >= 0x0000 && addr <= sizeInBytes) {
            ram[addr] = data;
        } else {
            throw new MemoryAddressExceedsMemoryException("address " + addr + " exceeds memory size");
        }
    }

    public int read(int addr, boolean readOnly) {
        if (addr >= 0x0000 && addr <= sizeInBytes) {
            return ram[addr];
        } else {
            throw new MemoryAddressExceedsMemoryException("address " + addr + " exceeds memory size");
        }
    }

    public int getNumberOfBytes() {
        return ram.length;
    }

    public void writeRomAt(int startAddr, byte[] copyOfRange) {
        for (int i = 0; i < copyOfRange.length; i++) {
            ram[startAddr + i] = copyOfRange[i];
        }
    }
}
