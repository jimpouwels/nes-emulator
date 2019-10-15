package nl.pouwels.nes.ppu;

import nl.pouwels.nes.cartridge.Cartridge;

public class Olc2c02 {

    public boolean frameComplete;
    private static final int SIZE_IN_BYTES = 0x0008;
    private Cartridge cartridge;
    private int cycles;
    private int scanline;
    private Pixel[] colorPallette = new Pixel[0x40];
    private int[][] nameTables = new int[2][1024];
    private int[] paletteTable = new int[32];
    private Screen screen;

    public Olc2c02(Screen screen) {
        colorPallette[0x00] = new Pixel(84, 84, 84);
        colorPallette[0x01] = new Pixel(0, 30, 116);
        colorPallette[0x02] = new Pixel(8, 16, 144);
        colorPallette[0x03] = new Pixel(48, 0, 136);
        colorPallette[0x04] = new Pixel(68, 0, 100);
        colorPallette[0x05] = new Pixel(92, 0, 48);
        colorPallette[0x06] = new Pixel(84, 4, 0);
        colorPallette[0x07] = new Pixel(60, 24, 0);
        colorPallette[0x08] = new Pixel(32, 42, 0);
        colorPallette[0x09] = new Pixel(8, 58, 0);
        colorPallette[0x0A] = new Pixel(0, 64, 0);
        colorPallette[0x0B] = new Pixel(0, 60, 0);
        colorPallette[0x0C] = new Pixel(0, 50, 60);
        colorPallette[0x0D] = new Pixel(0, 0, 0);
        colorPallette[0x0E] = new Pixel(0, 0, 0);
        colorPallette[0x0F] = new Pixel(0, 0, 0);

        colorPallette[0x10] = new Pixel(152, 150, 152);
        colorPallette[0x11] = new Pixel(8, 76, 196);
        colorPallette[0x12] = new Pixel(48, 50, 236);
        colorPallette[0x13] = new Pixel(92, 30, 228);
        colorPallette[0x14] = new Pixel(136, 20, 176);
        colorPallette[0x15] = new Pixel(160, 20, 100);
        colorPallette[0x16] = new Pixel(152, 34, 32);
        colorPallette[0x17] = new Pixel(120, 60, 0);
        colorPallette[0x18] = new Pixel(84, 90, 0);
        colorPallette[0x19] = new Pixel(40, 114, 0);
        colorPallette[0x1A] = new Pixel(8, 124, 0);
        colorPallette[0x1B] = new Pixel(0, 118, 40);
        colorPallette[0x1C] = new Pixel(0, 102, 120);
        colorPallette[0x1D] = new Pixel(0, 0, 0);
        colorPallette[0x1E] = new Pixel(0, 0, 0);
        colorPallette[0x1F] = new Pixel(0, 0, 0);

        colorPallette[0x20] = new Pixel(236, 238, 236);
        colorPallette[0x21] = new Pixel(76, 154, 236);
        colorPallette[0x22] = new Pixel(120, 124, 236);
        colorPallette[0x23] = new Pixel(176, 98, 236);
        colorPallette[0x24] = new Pixel(228, 84, 236);
        colorPallette[0x25] = new Pixel(236, 88, 180);
        colorPallette[0x26] = new Pixel(236, 106, 100);
        colorPallette[0x27] = new Pixel(212, 136, 32);
        colorPallette[0x28] = new Pixel(160, 170, 0);
        colorPallette[0x29] = new Pixel(116, 196, 0);
        colorPallette[0x2A] = new Pixel(76, 208, 32);
        colorPallette[0x2B] = new Pixel(56, 204, 108);
        colorPallette[0x2C] = new Pixel(56, 180, 204);
        colorPallette[0x2D] = new Pixel(60, 60, 60);
        colorPallette[0x2E] = new Pixel(0, 0, 0);
        colorPallette[0x2F] = new Pixel(0, 0, 0);

        colorPallette[0x30] = new Pixel(236, 238, 236);
        colorPallette[0x31] = new Pixel(168, 204, 236);
        colorPallette[0x32] = new Pixel(188, 188, 236);
        colorPallette[0x33] = new Pixel(212, 178, 236);
        colorPallette[0x34] = new Pixel(236, 174, 236);
        colorPallette[0x35] = new Pixel(236, 174, 212);
        colorPallette[0x36] = new Pixel(236, 180, 176);
        colorPallette[0x37] = new Pixel(228, 196, 144);
        colorPallette[0x38] = new Pixel(204, 210, 120);
        colorPallette[0x39] = new Pixel(180, 222, 120);
        colorPallette[0x3A] = new Pixel(168, 226, 144);
        colorPallette[0x3B] = new Pixel(152, 226, 180);
        colorPallette[0x3C] = new Pixel(160, 214, 228);
        colorPallette[0x3D] = new Pixel(160, 162, 160);
        colorPallette[0x3E] = new Pixel(0, 0, 0);
        colorPallette[0x3F] = new Pixel(0, 0, 0);
        this.screen = screen;
    }

    public void clock() {
        screen.drawPixel(cycles, scanline, colorPallette[((Math.random() % 2) > 0.5) ? 0x3F : 0x30]);

        if (cycles >= 340) {
            cycles = 0;
            if (scanline >= 260) {
                scanline = 0;
                frameComplete = true;
            }
            scanline++;
        }
        cycles++;
    }

    public void connectCartridge(Cartridge cartridge) {
        this.cartridge = cartridge;
    }

    public int cpuRead(int address_16) {
        int internalAddress_16 = mapToInternalRange(address_16);
        return 0x00;
    }

    public void cpuWrite(int address_16, int data_8) {
        int internalAddress_16 = mapToInternalRange(address_16);
    }

    public int ppuRead(int address_16) {
        if (cartridge.isInCharacterRomRange(address_16)) {
            return cartridge.ppuReadByte(address_16);
        }
        throw new RuntimeException("cannot read");
    }

    public void ppuWrite(int address_16, int data_8) {
        if (cartridge.isInCharacterRomRange(address_16)) {
            cartridge.ppuWriteByte(address_16, data_8);
        }
        throw new RuntimeException("cannot read");
    }

    private int mapToInternalRange(int address_16) {
        return address_16 & (SIZE_IN_BYTES - 1);
    }
}
