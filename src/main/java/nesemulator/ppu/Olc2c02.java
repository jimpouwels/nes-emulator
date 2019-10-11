package nesemulator.ppu;

import nesemulator.cartridge.Cartridge;

public class Olc2c02 {

    private static final int SIZE_IN_BYTES = 0x0008;
    private Cartridge cartridge;
    private int[][] nameTables = new int[2][1024];
    private int[] paletteTable = new int[32];

    public void clock() {
    }

    public void connectCartridge(Cartridge cartridge) {
    }

    public int cpuRead(int address_16) {
        int internalAddress_16 = mapToInternalRange(address_16);
        return 0x00;
    }

    public void cpuWrite(int address_16, int data_8) {
        int internalAddress_16 = mapToInternalRange(address_16);
    }

    public int ppuRead(int address_16) {
        int data = cartridge.ppuReadByte(address_16);
        if (data != -1) {
        }
        return 0x00;
    }

    public void ppuWrite(int address_16, int data_8) {
        if (cartridge.ppuWriteByte(address_16, data_8)) {
        }
    }

    private int mapToInternalRange(int address_16) {
        return address_16 & (SIZE_IN_BYTES - 1);
    }
}
