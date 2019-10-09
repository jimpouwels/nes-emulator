package nesemulator;

import nesemulator.exception.MemoryAddressExceedsMemoryException;

import static nesemulator.utils.ByteUtilities.widenIgnoreSigning;

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
            ram[startAddr + i] = widenIgnoreSigning(copyOfRange[i]);
        }
    }

    public void printRam() {
        System.out.println("\nRAM");
        System.out.println("-----------------------------");
        for (int i = 0; i < ram.length; i += 16) {
            for (int y = i; y < i + 16; y++) {
                System.out.print(ram[y] + "   ");
            }
            System.out.print(System.lineSeparator());
            System.out.print(System.lineSeparator());
        }
    }
}
