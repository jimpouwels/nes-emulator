import exception.MemoryAddressExceedsMemoryException;

public class Bus {

    private short[] ram;
    private long sizeInBytes;

    public Bus(int sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
        ram = new short[sizeInBytes];
    }

    public void write(int addr, short data) {
        if (addr >= 0x0000 && addr <= sizeInBytes) {
            ram[addr] = data;
        } else {
            throw new MemoryAddressExceedsMemoryException("address " + addr + " exceeds memory size");
        }
    }

    public short read(int addr, boolean readOnly) {
        if (addr >= 0x0000 && addr <= sizeInBytes) {
            return ram[addr];
        } else {
            throw new MemoryAddressExceedsMemoryException("address " + addr + " exceeds memory size");
        }
    }
}
