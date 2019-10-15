package nl.pouwels.nes;

/**
 * The RAM has an addressable range of 8KB, but the hardware is really only 2KB.
 * The left 2KB of addressable range is the actual memory, the other 3 bytes are mirrors of that 2KB.
 * Because we don't want to implement the actual mirroring, we just take the address, and % (mod) it, so that we bring
 * it down to 0 with offset.
 */
public class Ram {

    private static final int SIZE_IN_BYTES = 2048;
    private int[] memory = new int[SIZE_IN_BYTES];

    public void cpuWrite(int address_16, int data_8) {
        memory[mapToInternalRange(address_16)] = data_8;
    }

    public int cpuReadByte(int address_16) {
        return memory[mapToInternalRange(address_16)];
    }

    private int mapToInternalRange(int address_16) {
        return address_16 & (SIZE_IN_BYTES - 1);
    }
}
