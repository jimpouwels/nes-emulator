package nesemulator.ppu;

public class Olc2c02 {

    private static final int SIZE_IN_BYTES = 0x0008;
    private int[] ppuRam = new int[SIZE_IN_BYTES];

    public void cpuWrite(int address_16, int data_8) {
        ppuRam[mapToInternalRange(address_16)] = data_8;
    }

    public int cpuRead(int address_16) {
        return ppuRam[mapToInternalRange(address_16)];
    }

    private int mapToInternalRange(int address_16) {
        return address_16 & (SIZE_IN_BYTES - 1);
    }
}
