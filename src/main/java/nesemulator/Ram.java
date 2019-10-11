package nesemulator;

public class Ram {

    private static final int SIZE_IN_BYTES = 2048;
    private int[] memory = new int[SIZE_IN_BYTES];

    public void cpuWrite(int address_16, int data_8) {
        memory[mapToInternalRange(address_16)] = data_8;
    }

    public int cpuRead(int address_16) {
        return memory[mapToInternalRange(address_16)];
    }

    private int mapToInternalRange(int address_16) {
        return address_16 & (SIZE_IN_BYTES - 1);
    }
}
