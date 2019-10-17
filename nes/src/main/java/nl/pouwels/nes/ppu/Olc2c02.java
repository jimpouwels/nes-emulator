package nl.pouwels.nes.ppu;

import nl.pouwels.nes.cartridge.Cartridge;
import nl.pouwels.nes.ppu.register.ControlRegister;
import nl.pouwels.nes.ppu.register.MaskRegister;
import nl.pouwels.nes.ppu.register.StatusRegister;

public class Olc2c02 {

    private static final int FOUR_KB = 0x1000;
    private static final int PATTERN_TABLE_SIZE_IN_KB = FOUR_KB;
    private static final int PALLETTE_MEMORY_ADDRESS_START = 0x3F00;
    private static final int SIZE_IN_BYTES = 0x0008;
    private boolean frameComplete;
    private AddressWriteMode addressWriteMode = AddressWriteMode.HIGH_BYTE;
    private int cpuWrittenAddress_16;
    private Cartridge cartridge;
    private int cycles;
    private int scanline;
    // reading the data from the ppu is delayed by 1 cycle, buffer it
    private int dataBuffer_8;
    private int address_16;

    private ControlRegister controlRegister = new ControlRegister();
    private MaskRegister maskRegister = new MaskRegister();
    private StatusRegister statusRegister = new StatusRegister();

    // memories
    private int[][] nameTablesMemory = new int[2][1024];
    private int[] paletteTableMemory = new int[32];
    private int[][] patternMemory = new int[2][4096];

    private Color[] colorPallette = new Color[0x40];
    private Sprite[] loadedPatternTables = {new Sprite(128, 128), new Sprite(128, 128)};
    private Screen screen;

    public Olc2c02(Screen screen) {
        colorPallette[0x00] = new Color(84, 84, 84);
        colorPallette[0x01] = new Color(0, 30, 116);
        colorPallette[0x02] = new Color(8, 16, 144);
        colorPallette[0x03] = new Color(48, 0, 136);
        colorPallette[0x04] = new Color(68, 0, 100);
        colorPallette[0x05] = new Color(92, 0, 48);
        colorPallette[0x06] = new Color(84, 4, 0);
        colorPallette[0x07] = new Color(60, 24, 0);
        colorPallette[0x08] = new Color(32, 42, 0);
        colorPallette[0x09] = new Color(8, 58, 0);
        colorPallette[0x0A] = new Color(0, 64, 0);
        colorPallette[0x0B] = new Color(0, 60, 0);
        colorPallette[0x0C] = new Color(0, 50, 60);
        colorPallette[0x0D] = new Color(0, 0, 0);
        colorPallette[0x0E] = new Color(0, 0, 0);
        colorPallette[0x0F] = new Color(0, 0, 0);

        colorPallette[0x10] = new Color(152, 150, 152);
        colorPallette[0x11] = new Color(8, 76, 196);
        colorPallette[0x12] = new Color(48, 50, 236);
        colorPallette[0x13] = new Color(92, 30, 228);
        colorPallette[0x14] = new Color(136, 20, 176);
        colorPallette[0x15] = new Color(160, 20, 100);
        colorPallette[0x16] = new Color(152, 34, 32);
        colorPallette[0x17] = new Color(120, 60, 0);
        colorPallette[0x18] = new Color(84, 90, 0);
        colorPallette[0x19] = new Color(40, 114, 0);
        colorPallette[0x1A] = new Color(8, 124, 0);
        colorPallette[0x1B] = new Color(0, 118, 40);
        colorPallette[0x1C] = new Color(0, 102, 120);
        colorPallette[0x1D] = new Color(0, 0, 0);
        colorPallette[0x1E] = new Color(0, 0, 0);
        colorPallette[0x1F] = new Color(0, 0, 0);

        colorPallette[0x20] = new Color(236, 238, 236);
        colorPallette[0x21] = new Color(76, 154, 236);
        colorPallette[0x22] = new Color(120, 124, 236);
        colorPallette[0x23] = new Color(176, 98, 236);
        colorPallette[0x24] = new Color(228, 84, 236);
        colorPallette[0x25] = new Color(236, 88, 180);
        colorPallette[0x26] = new Color(236, 106, 100);
        colorPallette[0x27] = new Color(212, 136, 32);
        colorPallette[0x28] = new Color(160, 170, 0);
        colorPallette[0x29] = new Color(116, 196, 0);
        colorPallette[0x2A] = new Color(76, 208, 32);
        colorPallette[0x2B] = new Color(56, 204, 108);
        colorPallette[0x2C] = new Color(56, 180, 204);
        colorPallette[0x2D] = new Color(60, 60, 60);
        colorPallette[0x2E] = new Color(0, 0, 0);
        colorPallette[0x2F] = new Color(0, 0, 0);

        colorPallette[0x30] = new Color(236, 238, 236);
        colorPallette[0x31] = new Color(168, 204, 236);
        colorPallette[0x32] = new Color(188, 188, 236);
        colorPallette[0x33] = new Color(212, 178, 236);
        colorPallette[0x34] = new Color(236, 174, 236);
        colorPallette[0x35] = new Color(236, 174, 212);
        colorPallette[0x36] = new Color(236, 180, 176);
        colorPallette[0x37] = new Color(228, 196, 144);
        colorPallette[0x38] = new Color(204, 210, 120);
        colorPallette[0x39] = new Color(180, 222, 120);
        colorPallette[0x3A] = new Color(168, 226, 144);
        colorPallette[0x3B] = new Color(152, 226, 180);
        colorPallette[0x3C] = new Color(160, 214, 228);
        colorPallette[0x3D] = new Color(160, 162, 160);
        colorPallette[0x3E] = new Color(0, 0, 0);
        colorPallette[0x3F] = new Color(0, 0, 0);
        this.screen = screen;
    }

    public void clock() {
        screen.drawPixel(cycles, scanline, colorPallette[((Math.random() % 2) > 0.5) ? 0x3F : 0x30]);

        cycles++;
        if (cycles >= 341) {
            cycles = 0;
            scanline++;
            if (scanline >= 261) {
                scanline = -1;
                frameComplete = true;
            }
        }
    }

    public void connectCartridge(Cartridge cartridge) {
        this.cartridge = cartridge;
    }

    /**
     * The pattern memory consists of two pattern tables, 4kb each.
     * The pattern memory is a 1d representation of a 2d table. There's 16x16 tiles.
     * Each tile is 8x8 bytes.
     * <p>
     * 2-Bit Pixels              LSB Bit Plane 8x8 bytes      MSB Bit Plane 8x8 bytes
     * 0 0 0 0 0 0 0 0	         0 0 0 0 0 0 0 0              0 0 0 0 0 0 0 0
     * 0 1 1 0 0 1 1 0	         0 1 1 0 0 1 1 0              0 0 0 0 0 0 0 0
     * 0 1 2 0 0 2 1 0	         0 1 1 0 0 1 1 0              0 0 1 0 0 1 0 0
     * 0 0 0 0 0 0 0 0     =     0 0 0 0 0 0 0 0      +       0 0 0 0 0 0 0 0
     * 0 1 1 0 0 1 1 0	         0 1 1 0 0 1 1 0              0 0 0 0 0 0 0 0
     * 0 0 1 1 1 1 0 0	         0 0 1 1 1 1 0 0              0 0 0 0 0 0 0 0
     * 0 0 0 2 2 0 0 0	         0 0 0 1 1 0 0 0              0 0 0 1 1 0 0 0
     * 0 0 0 0 0 0 0 0	         0 0 0 0 0 0 0 0              0 0 0 0 0 0 0 0
     * <p>
     * the order in memory is:
     * row1Table1 - row1Table2 - row2Table1 - row2Table2 - etc.
     * This means each byte in the MSB bit plane, is 8 bytes ahead of the corresponding byte in LSB bit plane.
     */
    public Sprite getPatternTable(int tableIndex, int pallette_8) {
        for (int tileY = 0; tileY < 16; tileY++) {
            for (int tileX = 0; tileX < 16; tileX++) {
                int tileOffset = tileY * 256 + tileX * 16;
                for (int tileRow = 0; tileRow < 8; tileRow++) {
                    int tableOffset = tableIndex * PATTERN_TABLE_SIZE_IN_KB;
                    int tileRowLsb_8 = ppuRead(tableOffset + tileOffset + tileRow);
                    int tileRowMsb_8 = ppuRead(tableOffset + tileOffset + tileRow + 8);
                    for (int tileColumn = 0; tileColumn < 8; tileColumn++) {
                        // combine each bit of the lsb with the msb, this way you get the pixelvalue for each column
                        int pixelValue_8 = (tileRowLsb_8 & 0x01) + ((tileRowMsb_8 & 0x01) * 2); // msb bit 1 represents a 2
                        tileRowLsb_8 >>= 1;
                        tileRowMsb_8 >>= 1;

                        loadedPatternTables[tableIndex].setPixel(
                                tileX * 8 + (7 - tileColumn),
                                tileY * 8 + tileRow,
                                loadColorFromPallette(pallette_8, pixelValue_8));
                    }
                }
            }
        }
        screen.drawPatternTable(tableIndex, loadedPatternTables[tableIndex]);
        return loadedPatternTables[tableIndex]; // FIXME: Can't we just load it once at startup?
    }

    public int cpuReadByte(int address_16) {
        int data_8 = 0x00;
        switch (address_16) {
            case 0x0000: // read control register
                break;
            case 0x0001: // read mask register
                break;
            case 0x0002: // status register
                statusRegister.verticalBlank_1 = 1;
                // the first 5 bits of the status register are unused, but it's 'likely' that in the hardware it's filled with the last databuffer value.
                data_8 = statusRegister.getAsByte() | (dataBuffer_8 & 0x1F);
                statusRegister.verticalBlank_1 = 0;
                addressWriteMode = AddressWriteMode.HIGH_BYTE;
                break;
            case 0x0007: // ppu read data
                // delayed data retrieval
                data_8 = dataBuffer_8;
                dataBuffer_8 = ppuRead(cpuWrittenAddress_16);

                // the pallette memory can return the data within the same clockcycle, no delay
                if (isPalletteMemoryAddress(cpuWrittenAddress_16)) {
                    data_8 = dataBuffer_8;
                }
                cpuWrittenAddress_16++;
                break;
        }
        return data_8;
    }

    public void cpuWrite(int address_16, int data_8) {
        switch (address_16) {
            case 0x0000: // write control register
                controlRegister.write(data_8);
                break;
            case 0x0001: // write mask register
                maskRegister.write(data_8);
                break;
            case 0x0006: // write address
                writeAddress(address_16, data_8);
                break;
            case 0x0007: // ppu write data
                ppuWrite(cpuWrittenAddress_16, data_8);
                cpuWrittenAddress_16++;
                break;
        }
    }

    public int ppuRead(int address_16) {
        if (cartridge.isInCharacterRomRange(address_16)) {
            return cartridge.ppuReadByte(address_16);
        } else if (isPatternMemoryAddress(address_16)) {
            // the 12th bit == 4096. So if that bit is 1, we want the second table (starting at 4096, if it's 0, we want the first table.
            // the second index is the remaining bits
            return patternMemory[(address_16 & 0x1000) >> 12][address_16 & 0x0FFF];
        } else if (isNameTableAddress(address_16)) {

        } else if (isPalletteMemoryAddress(address_16)) {
            return loadPallette(address_16);
        }
        throw new RuntimeException("cannot read");
    }

    public void ppuWrite(int address_16, int data_8) {
        if (cartridge.isInCharacterRomRange(address_16)) {
            cartridge.ppuWriteByte(address_16, data_8);
        } else if (isPatternMemoryAddress(address_16)) {
            // the 12th bit == 4096. So if that bit is 1, we want the second table (starting at 4096, if it's 0, we want the first table.
            // the second index is the remaining bits
            patternMemory[(address_16 & 0x1000) >> 12][address_16 & 0x0FFF] = data_8;
        } else if (isNameTableAddress(address_16)) {

        } else if (isPalletteMemoryAddress(address_16)) {
            writePallette(address_16, data_8);
        }
        throw new RuntimeException("cannot read");
    }

    /**
     * pallette_8 * 4 happens because each pallette has 4 bytes, so when we choose pallette 2,
     * you want to skip 8 bytes ahead.
     */
    private Color loadColorFromPallette(int pallette_8, int pixelValue_8) {
        return colorPallette[ppuRead(PALLETTE_MEMORY_ADDRESS_START + (pallette_8 << 2) + pixelValue_8) & 0x3F];
    }

    private void writeAddress(int address_16, int data_8) {
        if (addressWriteMode == AddressWriteMode.LOW_BYTE) {
            cpuWrittenAddress_16 = (address_16 & 0x00FF) | data_8;
            addressWriteMode = AddressWriteMode.HIGH_BYTE;
        } else {
            cpuWrittenAddress_16 = (address_16 & 0xFF00) | data_8 << 8;
            addressWriteMode = AddressWriteMode.LOW_BYTE;
        }
    }

    private int loadPallette(int address_16) {
        int palletteIndex = toPalletteIndex(address_16);
        return paletteTableMemory[palletteIndex];
    }

    private void writePallette(int address_16, int data_8) {
        int palletteIndex = toPalletteIndex(address_16);
        paletteTableMemory[palletteIndex] = data_8;
    }

    private int toPalletteIndex(int address_16) {
        address_16 &= 0x001F;
        if (address_16 == 0x0010) {
            address_16 = 0x0000;
        } else if (address_16 == 0x0014) {
            address_16 = 0x0004;
        } else if (address_16 == 0x0018) {
            address_16 = 0x0008;
        } else if (address_16 == 0x001C) {
            address_16 = 0x000C;
        }
        return address_16;
    }

    private boolean isPatternMemoryAddress(int address_16) {
        return address_16 >= 0x0000 && address_16 <= 0x1FFF;
    }

    private boolean isNameTableAddress(int address_16) {
        return address_16 >= 0x2000 && address_16 <= 0x3EFF;
    }

    private boolean isPalletteMemoryAddress(int address_16) {
        return address_16 >= 0x3F00 && address_16 <= 0x3FFF;
    }

    private int mapToInternalRange(int address_16) {
        return address_16 & (SIZE_IN_BYTES - 1);
    }
}
