package nesemulator.ppu;

public class Olc2c02 {

    private static final int SIZE_IN_BYTES = 0x0007;
    private static final int PPU_RANGE_START = 0x2000;
    private static final int PPU_RANGE_END = 0x3FFF;
    private int[] ppuRam = new int[SIZE_IN_BYTES];

    public void cpuWrite(int address_16, int data_8) {
        ppuRam[mapToInternalRange(address_16)] = data_8;
    }

    public int cpuRead(int address_16) {
        return ppuRam[mapToInternalRange(address_16)];
    }

    public boolean isAddressInRange(int address_16) {
        return address_16 >= Olc2c02.PPU_RANGE_START && address_16 <= Olc2c02.PPU_RANGE_END;
    }

    private int mapToInternalRange(int address_16) {
        return address_16 & (SIZE_IN_BYTES - 1);
    }
}
