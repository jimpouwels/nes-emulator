package nl.pouwels.nes.ppu;

import nl.pouwels.nes.cartridge.Cartridge;
import nl.pouwels.nes.ppu.register.ControlRegister;
import nl.pouwels.nes.ppu.register.LoopyRegister;
import nl.pouwels.nes.ppu.register.MaskRegister;
import nl.pouwels.nes.ppu.register.StatusRegister;

public class Olc2c02 {

    private static final int FOUR_KB = 0x1000;
    private static final int PATTERN_TABLE_SIZE_IN_KB = FOUR_KB;
    private static final int PALLETTE_MEMORY_ADDRESS_START = 0x3F00;
    private static final int NAMETABLE_MEMORY_RANGE_START = 0x2000;

    public boolean nonMaskableInterrupt;
    private boolean frameComplete;
    private AddressWriteMode addressWriteMode = AddressWriteMode.HIGH_BYTE;
    private Cartridge cartridge;
    private int cycle;
    private int scanline;
    // reading the data from the ppu is delayed by 1 cycle, buffer it
    private int dataBuffer_8;

    private ControlRegister controlRegister_8 = new ControlRegister();
    private MaskRegister maskRegister_8 = new MaskRegister();
    private StatusRegister statusRegister_8 = new StatusRegister();
    private LoopyRegister vRam_16 = new LoopyRegister();
    private LoopyRegister tRam_16 = new LoopyRegister();
    private int fineX_8;

    private int bgNextTileId;
    private int bgNextTileAttribute;
    private int bgNextTileLsb_8;
    private int bgNextTileMsb_8;

    private int bgShifterPatternLow_8;
    private int bgShifterPatternHigh_8;
    private int bgShifterAttributeLow_8;
    private int bgShifterAttributeHigh_8;

    // memories
    private int[][] nameTablesMemory = new int[2][1024];
    private int[] paletteTableMemory = new int[32];
    private int[][] patternMemory = new int[2][4096];
    public ObjectAttributeEntry[] objectAttributeMemory = new ObjectAttributeEntry[64];

    private ObjectAttributeEntry[] spriteScanlines = new ObjectAttributeEntry[8];
    private int spriteCount;

    private int[] spriteShifterPatternLow = new int[8];
    private int[] spriteShifterPatternHigh = new int[8];

    private boolean spriteZeroHitPossible = false;
    private boolean spriteZeroBeingRendered = false;


    private int oamAddress_8;

    private Color[] colorPallette = new Color[0x40];
    private Sprite[] loadedPatternTables = {new Sprite(128, 128), new Sprite(128, 128)};
    private Screen screen;

    public Olc2c02(Screen screen) {
        for (int i = 0; i < objectAttributeMemory.length; i++) {
            objectAttributeMemory[i] = new ObjectAttributeEntry();
        }
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

    public void reset() {
        fineX_8 = 0x00;
        addressWriteMode = AddressWriteMode.HIGH_BYTE;
        dataBuffer_8 = 0x00;
        scanline = 0;
        cycle = 0;
        bgNextTileId = 0x00;
        bgNextTileAttribute = 0x00;
        bgNextTileLsb_8 = 0x00;
        bgNextTileMsb_8 = 0x00;
        bgShifterPatternLow_8 = 0x0000;
        bgShifterPatternHigh_8 = 0x0000;
        bgShifterAttributeLow_8 = 0x0000;
        bgShifterAttributeHigh_8 = 0x0000;
        statusRegister_8.write(0);
        maskRegister_8.write(0);
        controlRegister_8.write(0);
        vRam_16.write(0);
        tRam_16.write(0);
    }

    public void clock() {
        if (scanline >= -1 && scanline < 240) {

            if (cycle == 260 && (maskRegister_8.renderBackground_1 == 1 || maskRegister_8.renderSprites_1 == 1)) {
                cartridge.mapper.scanlineSignal();
            }
            if (scanline == 0 && cycle == 0) {
                // "Odd Frame" cycle skip
                cycle = 1;
            }
            if (scanline == -1 && cycle == 1) {
                statusRegister_8.verticalBlank_1 = 0;
                statusRegister_8.spriteZeroHit_1 = 0;
                statusRegister_8.spriteOverflow_1 = 0;

                for (int i = 0; i < 8; i++) {
                    spriteShifterPatternLow[i] = 0;
                    spriteShifterPatternHigh[i] = 0;
                }
            }
            if ((cycle >= 2 && cycle < 258) || (cycle >= 321) && cycle < 338) {
                updateShifters();
                switch ((cycle - 1) % 8) {
                    case 0:
                        loadBackgroundShifters();
                        bgNextTileId = ppuRead(0x2000 | (vRam_16.get() & 0x0FFF));
                        break;
                    case 2:
                        bgNextTileAttribute = ppuRead((NAMETABLE_MEMORY_RANGE_START + 0x03C0)
                                | (vRam_16.nametableY_1 << 11)
                                | (vRam_16.nametableX_1 << 10)
                                | ((vRam_16.coarseY_5 >> 2) << 3)
                                | (vRam_16.coarseX_5 >> 2));
                        if ((vRam_16.coarseY_5 & 0x02) > 0) {
                            bgNextTileAttribute >>= 4;
                        }
                        if ((vRam_16.coarseX_5 & 0x02) > 0) {
                            bgNextTileAttribute >>= 2;
                        }
                        bgNextTileAttribute &= 0x03;
                        break;
                    case 4:
                        bgNextTileLsb_8 = ppuRead((controlRegister_8.patternBackground_1 << 12)
                                + (bgNextTileId << 4)
                                + (vRam_16.fineY_3));
                        break;
                    case 6:
                        bgNextTileMsb_8 = ppuRead((controlRegister_8.patternBackground_1 << 12)
                                + (bgNextTileId << 4)
                                + vRam_16.fineY_3 + 8);
                        break;
                    case 7:
                        incrementScrollX();
                        break;
                }
            }

            if (cycle == 256) {
                incrementScrollY();
            }
            if (cycle == 257) {
                loadBackgroundShifters();
                transferAddressX();
            }
            if (cycle == 338 || cycle == 340) {
                bgNextTileId = ppuRead(0x2000 | (vRam_16.get() & 0x0FFF));
            }

            if (scanline == -1 && cycle >= 280 && cycle < 305) {
                transferAddressY();
            }

            // foreground rendering
            if (cycle == 257 && scanline >= 0) {
                for (int i = 0; i < spriteScanlines.length; i++) {
                    spriteScanlines[i] = new ObjectAttributeEntry();
                }
                for (ObjectAttributeEntry oae : spriteScanlines) {
                    oae.y_8 = 0xFF;
                }
                spriteCount = 0;

                for (int i = 0; i < 8; i++) {
                    spriteShifterPatternLow[i] = 0;
                    spriteShifterPatternHigh[i] = 0;
                }

                int oamEntry = 0;

                spriteZeroHitPossible = false;

                while (oamEntry < 64 && spriteCount < 9) {
                    int signedScanline = scanline;
                    if (signedScanline > 0x7FFF) {
                        signedScanline &= 0x7FFF;
                        signedScanline -= (signedScanline * 2);
                    }
                    int signedY = objectAttributeMemory[oamEntry].y_8;
                    if (signedY > 0x7FF) {
                        signedY &= 0x7FF;
                        signedY -= (signedY * 2);
                    }
                    int diff = signedScanline - signedY;
                    if (diff >= 0 && diff < ((controlRegister_8.spriteSize_1 == 1) ? 16 : 8)) {
                        if (spriteCount < 8) {
                            if (oamEntry == 0) {
                                spriteZeroHitPossible = true;
                            }
                            ObjectAttributeEntry entry = new ObjectAttributeEntry();
                            entry.x_8 = objectAttributeMemory[oamEntry].x_8;
                            entry.attribute = objectAttributeMemory[oamEntry].attribute;
                            entry.id_8 = objectAttributeMemory[oamEntry].id_8;
                            entry.y_8 = objectAttributeMemory[oamEntry].y_8;
                            spriteScanlines[spriteCount] = entry;
                            spriteCount++;
                        }
                    }
                    oamEntry++;
                }
                statusRegister_8.spriteOverflow_1 = spriteCount > 8 ? 1 : 0;
            }

            if (cycle == 340) {
                for (int i = 0; i < spriteCount; i++) {
                    int spritePatternBitsLow_8;
                    int spritePatternBitsHigh_8;
                    int spritePatternAddressLow_16;
                    int spritePatternAddressHigh_16;

                    if (!(controlRegister_8.spriteSize_1 == 1)) { // 8x8
                        if (!((spriteScanlines[i].attribute & 0x80) > 0)) {
                            // sprite not flipped
                            spritePatternAddressLow_16 =
                                    (controlRegister_8.patternSprite_1 << 12)
                                            | (spriteScanlines[i].id_8 << 4)
                                            | (scanline - spriteScanlines[i].y_8);
                        } else {
                            // sprite upside down
                            spritePatternAddressLow_16 =
                                    (controlRegister_8.patternSprite_1 << 12)
                                            | (spriteScanlines[i].id_8 << 4)
                                            | (7 - (scanline - spriteScanlines[i].y_8));
                        }
                    } else { // 8x16
                        if (!((spriteScanlines[i].attribute & 0x80) > 0)) {
                            // sprite not flipped
                            if (scanline - spriteScanlines[i].y_8 < 8) {
                                // reading top half of tile
                                spritePatternAddressLow_16 =
                                        ((spriteScanlines[i].id_8 & 0x01) << 12)
                                                | ((spriteScanlines[i].id_8 & 0xFE) << 4)
                                                | ((scanline - spriteScanlines[i].y_8) & 0x07);
                            } else {
                                // reading bottom half of tile
                                spritePatternAddressLow_16 =
                                        ((spriteScanlines[i].id_8 & 0x01) << 12)
                                                | (((spriteScanlines[i].id_8 & 0xFE) + 1) << 4)
                                                | ((scanline - spriteScanlines[i].y_8) & 0x07);
                            }
                        } else {
                            // sprite upside down
                            if (scanline - spriteScanlines[i].y_8 < 8) {
                                spritePatternAddressLow_16 =
                                        ((spriteScanlines[i].id_8 & 0x01) << 12)
                                                | (((spriteScanlines[i].id_8 & 0xFE) + 1) << 4)
                                                | (7 - (scanline - spriteScanlines[i].y_8) & 0x07);
                            } else {
                                spritePatternAddressLow_16 =
                                        ((spriteScanlines[i].id_8 & 0x01) << 12)
                                                | ((spriteScanlines[i].id_8 & 0xFE) << 4)
                                                | (7 - (scanline - spriteScanlines[i].y_8) & 0x07);
                            }
                        }
                    }
                    spritePatternAddressHigh_16 = spritePatternAddressLow_16 + 8;
                    spritePatternBitsLow_8 = ppuRead(spritePatternAddressLow_16);
                    spritePatternBitsHigh_8 = ppuRead(spritePatternAddressHigh_16);

                    if ((spriteScanlines[i].attribute & 0x40) > 0) {
                        spritePatternBitsLow_8 = flipByte(spritePatternBitsLow_8);
                        spritePatternBitsHigh_8 = flipByte(spritePatternBitsHigh_8);
                    }

                    spriteShifterPatternLow[i] = spritePatternBitsLow_8;
                    spriteShifterPatternHigh[i] = spritePatternBitsHigh_8;
                }
            }

        }

        if (scanline == 240) {
            // Post rendering scanline - Do Nothing!
        }

        if (scanline >= 241 && scanline < 261) {
            if (scanline == 241 && cycle == 1) {
                statusRegister_8.verticalBlank_1 = 1;
                if (controlRegister_8.enableNmi_1 == 1) {
                    nonMaskableInterrupt = true;
                }
            }
        }

        int bgPixel = 0x00;
        int bgPalette = 0x00;

        if (maskRegister_8.renderBackground_1 == 1) {
            int bitMux = 0x8000 >> fineX_8;
            int p0Pixel = (bgShifterPatternLow_8 & bitMux) > 0 ? 1 : 0;
            int p1Pixel = (bgShifterPatternHigh_8 & bitMux) > 0 ? 1 : 0;
            bgPixel = (p1Pixel << 1) | p0Pixel;

            int bgPalette0 = (bgShifterAttributeLow_8 & bitMux) > 0 ? 1 : 0;
            int bgPalette1 = (bgShifterAttributeHigh_8 & bitMux) > 0 ? 1 : 0;
            bgPalette = (bgPalette1 << 1) | bgPalette0;
        }

        int fgPixel = 0;
        int fgPalette = 0;
        int fgPriority = 0;

        if (maskRegister_8.renderSprites_1 == 1) {

            spriteZeroBeingRendered = false;

            for (int i = 0; i < spriteCount; i++) {
                if (spriteScanlines[i].x_8 == 0) {
                    int fgPixelLow = (spriteShifterPatternLow[i] & 0x80) > 0 ? 1 : 0;
                    int fgPixelHigh = (spriteShifterPatternHigh[i] & 0x80) > 0 ? 1 : 0;
                    fgPixel = (fgPixelHigh << 1) | fgPixelLow;
                    fgPalette = (spriteScanlines[i].attribute & 0x03) + 0x04;
                    fgPriority = (spriteScanlines[i].attribute & 0x20) == 0 ? 1 : 0;

                    if (fgPixel != 0) {
                        if (i == 0) {
                            spriteZeroBeingRendered = true;
                        }
                        break;
                    }
                }
            }
        }

        int pixel = 0;
        int palette = 0;

        if (bgPixel == 0 && fgPixel == 0) {
            pixel = 0;
            palette = 0;
        } else if (bgPixel == 0 && fgPixel > 0) {
            pixel = fgPixel;
            palette = fgPalette;
        } else if (bgPixel > 0 & fgPixel == 0) {
            pixel = bgPixel;
            palette = bgPalette;
        } else if (bgPixel > 0 & fgPixel > 0) {
            if (fgPriority == 1) {
                pixel = fgPixel;
                palette = fgPalette;
            } else {
                pixel = bgPixel;
                palette = bgPalette;
            }

            if (spriteZeroHitPossible && spriteZeroBeingRendered) {
                if ((maskRegister_8.renderBackground_1 & maskRegister_8.renderSprites_1) == 1) {
                    if ((maskRegister_8.renderBackgroundLeft_1 | maskRegister_8.renderSpritesLeft_1) > 1) {
                        if (cycle >= 9 && cycle < 258) {
                            statusRegister_8.spriteZeroHit_1 = 1;
                        }
                    } else {
                        if (cycle >= 1 && cycle < 258) {
                            statusRegister_8.spriteZeroHit_1 = 1;
                        }
                    }
                }
            }
        }

        screen.drawPixel(cycle - 1, scanline, loadColorFromPallette(palette, pixel));

        frameComplete = false;
        cycle++;
        if (cycle >= 341) {
            cycle = 0;
            scanline++;
            if (scanline >= 261) {
                scanline = -1;
                frameComplete = true;
            }
        }

    }

    private int flipByte(int byteToFlip) {
        byteToFlip = (byteToFlip & 0xF0) >> 4 | (byteToFlip & 0x0F) << 4;
        byteToFlip = (byteToFlip & 0xCC) >> 2 | (byteToFlip & 0x33) << 2;
        byteToFlip = (byteToFlip & 0xAA) >> 1 | (byteToFlip & 0x55) << 1;
        return byteToFlip;
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
        int tableOffset = tableIndex * PATTERN_TABLE_SIZE_IN_KB;
        for (int tileY = 0; tileY < 16; tileY++) {
            for (int tileX = 0; tileX < 16; tileX++) {
                int tileOffset = tileY * 256 + tileX * 16;
                for (int pixelX = 0; pixelX < 8; pixelX++) {
                    int pixelRowLsb = ppuRead(tableOffset + tileOffset + pixelX);
                    int pixelRowMsb = ppuRead(tableOffset + tileOffset + pixelX + 8);
                    for (int pixelY = 0; pixelY < 8; pixelY++) {
                        // combine each bit of the lsb with the msb, this way you get the pixelvalue for each column
                        int pixelValue_8 = (pixelRowLsb & 0x01) + ((pixelRowMsb & 0x01) * 2);

                        // since we loaded an entire row of data, we shift all the bits to the right to get the next bit combination
                        pixelRowLsb >>= 1;
                        pixelRowMsb >>= 1;
                        loadedPatternTables[tableIndex].setPixel(
                                tileX * 8 + (7 - pixelY),
                                tileY * 8 + pixelX,
                                loadColorFromPallette(pallette_8, pixelValue_8));
                    }
                }
            }
        }
        screen.drawPatternTable(tableIndex, loadedPatternTables[tableIndex]);
        return loadedPatternTables[tableIndex]; // FIXME: Do we need to return and set a field? Guess not...
    }

    public int cpuReadByte(int address_16) {
        int data_8 = 0x00;
        switch (address_16 & 0x0007) {
            case 0x0000: // read control register
                break;
            case 0x0001: // read mask register
                break;
            case 0x0002: // status register
                // the first 5 bits of the status register are unused, but it's 'likely' that in the hardware it's filled with the last databuffer value.
                data_8 = statusRegister_8.get() & 0xE0 | (dataBuffer_8 & 0x1F);
                statusRegister_8.verticalBlank_1 = 0;
                addressWriteMode = AddressWriteMode.HIGH_BYTE;
                break;
            case 0x0004:
                data_8 = objectAttributeMemory[(oamAddress_8 - (oamAddress_8 % 4)) / 4].get(oamAddress_8 % 4);
                break;
            case 0x0007: // ppu read data
                // delayed data retrieval
                data_8 = dataBuffer_8;
                dataBuffer_8 = ppuRead(vRam_16.get());

                // the pallette memory can return the data within the same clockcycle, no delay
                if (isPalletteMemoryAddress(vRam_16.get())) {
                    data_8 = dataBuffer_8;
                }
                vRam_16.incrementWith(controlRegister_8.incrementMode_1 > 0 ? 32 : 1); // if increment mode, increment on Y axis (32 tiles).
                break;
        }
        return data_8;
    }

    public void cpuWrite(int address_16, int data_8) {
        switch (address_16 & 0x0007) {
            case 0x0000: // write control register
                controlRegister_8.write(data_8);
                tRam_16.nametableX_1 = controlRegister_8.nametableX_1;
                tRam_16.nametableY_1 = controlRegister_8.nametableY_1;
                break;
            case 0x0001: // write mask register
                maskRegister_8.write(data_8);
                break;
            case 0x0003:
                oamAddress_8 = data_8;
                break;
            case 0x0004:
                objectAttributeMemory[(oamAddress_8 - (oamAddress_8 % 4)) / 4].set(oamAddress_8 % 4, data_8);
                break;
            case 0x0005:
                if (addressWriteMode == AddressWriteMode.HIGH_BYTE) {
                    fineX_8 = data_8 & 0x07; // FIXME: Mask needed?
                    tRam_16.coarseX_5 = data_8 >> 3;
                    addressWriteMode = AddressWriteMode.LOW_BYTE;
                } else {
                    tRam_16.fineY_3 = data_8 & 0x07;
                    tRam_16.coarseY_5 = data_8 >> 3;
                    addressWriteMode = AddressWriteMode.HIGH_BYTE;
                }
                break;
            case 0x0006: // write address
                if (addressWriteMode == AddressWriteMode.LOW_BYTE) {
                    tRam_16.write((tRam_16.get() & 0xFF00) | data_8);
                    vRam_16.write(tRam_16.get());
                    addressWriteMode = AddressWriteMode.HIGH_BYTE;
                } else {
                    tRam_16.write(((data_8 & 0x3F) << 8) | (tRam_16.get() & 0x00FF));
                    addressWriteMode = AddressWriteMode.LOW_BYTE;
                }
                break;
            case 0x0007: // ppu write data
                ppuWrite(vRam_16.get(), data_8);
                vRam_16.incrementWith(controlRegister_8.incrementMode_1 == 1 ? 32 : 1); // if increment mode, increment on Y axis (32 tiles).
                break;
        }
    }

    public int ppuRead(int address_16) {
        address_16 &= 0x3FFF;
        if (cartridge.isInCharacterRomRange(address_16)) {
            return cartridge.ppuReadByte(address_16);
        } else if (isPatternMemoryAddress(address_16)) {
            // the 12th bit == 4096. So if that bit is 1, we want the second table (starting at 4096, if it's 0, we want the first table.
            // the second index is the remaining bits
            return patternMemory[(address_16 & 0x1000) >> 12][address_16 & 0x0FFF];
        } else if (isNameTableAddress(address_16)) {
            address_16 &= 0x0FFF;
            if (isGameScrollingHorizontally()) { // vertically mirrored
                // the mask with 0x03FF is to map the address on 1kb array range
                if (address_16 >= 0x0000 && address_16 <= 0x03FF) { // reading from left top table
                    return nameTablesMemory[0][address_16 & 0x03FF]; // loading from 'left top' table
                } else if (address_16 >= 0x0400 && address_16 <= 0x07FF) { // reading from right top table
                    return nameTablesMemory[1][address_16 & 0x03FF]; // loading from 'right top' table
                } else if (address_16 >= 0x0800 & address_16 <= 0x0BFF) { // reading from bottom left table
                    return nameTablesMemory[0][address_16 & 0x03FF]; // loading from 'left top' table
                } else if (address_16 >= 0x0C00 && address_16 <= 0x0FFF) { // reading from right bottom table
                    return nameTablesMemory[1][address_16 & 0x03FF]; // loading from 'right top' table
                }
            } else if (isGameScrollingVertically()) {
                if (address_16 >= 0x0000 && address_16 <= 0x03FF) { // reading from left top table
                    return nameTablesMemory[0][address_16 & 0x03FF]; // loading from 'left top' table
                } else if (address_16 >= 0x0400 && address_16 <= 0x07FF) { // reading from right top table
                    return nameTablesMemory[0][address_16 & 0x03FF]; // loading from 'left top' table
                } else if (address_16 >= 0x0800 & address_16 <= 0x0BFF) { // reading from bottom left table
                    return nameTablesMemory[1][address_16 & 0x03FF]; // loading from 'left bottom' table
                } else if (address_16 >= 0x0C00 && address_16 <= 0x0FFF) { // reading from right bottom table
                    return nameTablesMemory[1][address_16 & 0x03FF]; // loading from 'left bottom' table
                }
            }
        } else if (isPalletteMemoryAddress(address_16)) {
            return loadPallette(address_16);
        }
        return 0x00;
    }

    public void ppuWrite(int address_16, int data_8) {
        address_16 &= 0x3FFF;
        if (cartridge.isInCharacterRomRange(address_16)) {
            cartridge.ppuWriteByte(address_16, data_8);
        } else if (isPatternMemoryAddress(address_16)) {
            // the 12th bit == 4096. So if that bit is 1, we want the second table (starting at 4096, if it's 0, we want the first table.
            // the second index is the remaining bits
            patternMemory[(address_16 & 0x1000) >> 12][address_16 & 0x0FFF] = data_8;
        } else if (isNameTableAddress(address_16)) {
            address_16 &= 0x0FFF;
            if (isGameScrollingHorizontally()) { // vertically mirrored
                // the mask with 0x03FF is to map the address on 1kb array range
                if (address_16 >= 0x0000 && address_16 <= 0x03FF) { // reading from left top table
                    nameTablesMemory[0][address_16 & 0x03FF] = data_8; // loading from 'left top' table
                } else if (address_16 >= 0x0400 && address_16 <= 0x07FF) { // reading from right top table
                    nameTablesMemory[1][address_16 & 0x03FF] = data_8; // loading from 'right top' table
                } else if (address_16 >= 0x0800 & address_16 <= 0x0BFF) { // reading from bottom left table
                    nameTablesMemory[0][address_16 & 0x03FF] = data_8; // loading from 'left top' table
                } else if (address_16 >= 0x0C00 && address_16 <= 0x0FFF) { // reading from right bottom table
                    nameTablesMemory[1][address_16 & 0x03FF] = data_8; // loading from 'right top' table
                }
            } else if (isGameScrollingVertically()) {
                if (address_16 >= 0x0000 && address_16 <= 0x03FF) { // reading from left top table
                    nameTablesMemory[0][address_16 & 0x03FF] = data_8; // loading from 'left top' table
                } else if (address_16 >= 0x0400 && address_16 <= 0x07FF) { // reading from right top table
                    nameTablesMemory[0][address_16 & 0x03FF] = data_8; // loading from 'left top' table
                } else if (address_16 >= 0x0800 & address_16 <= 0x0BFF) { // reading from bottom left table
                    nameTablesMemory[1][address_16 & 0x03FF] = data_8; // loading from 'left bottom' table
                } else if (address_16 >= 0x0C00 && address_16 <= 0x0FFF) { // reading from right bottom table
                    nameTablesMemory[1][address_16 & 0x03FF] = data_8; // loading from 'left bottom' table
                }
            }
        } else if (isPalletteMemoryAddress(address_16)) {
            writePallette(address_16, data_8);
        }
    }

    public boolean isFrameCompleted() {
        return frameComplete;
    }

    /**
     * pallette_8 * 4 happens because each pallette has 4 bytes, so when we choose pallette 2,
     * you want to skip 8 bytes ahead.
     */
    public Color loadColorFromPallette(int pallette_8, int pixelValue_8) {
        return colorPallette[ppuRead(PALLETTE_MEMORY_ADDRESS_START + (pallette_8 << 2) + pixelValue_8) & 0x3F];
    }

    private boolean isGameScrollingHorizontally() {
        return cartridge.nametableMirroringMode == NametableMirroringMode.VERTICAL;
    }

    private boolean isGameScrollingVertically() {
        return cartridge.nametableMirroringMode == NametableMirroringMode.HORIZONTAL;
    }

    private int loadPallette(int address_16) {
        int palletteIndex = toPalletteIndex(address_16);
        return paletteTableMemory[palletteIndex] & (maskRegister_8.grayScale_1 == 1 ? 0x30 : 0x3F);
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
        return address_16 >= NAMETABLE_MEMORY_RANGE_START && address_16 <= 0x3EFF;
    }

    private boolean isPalletteMemoryAddress(int address_16) {
        return address_16 >= 0x3F00 && address_16 <= 0x3FFF;
    }

    private void loadBackgroundShifters() {
        bgShifterPatternLow_8 = (bgShifterPatternLow_8 & 0xFF00) | bgNextTileLsb_8;
        bgShifterPatternHigh_8 = (bgShifterPatternHigh_8 & 0xFF00) | bgNextTileMsb_8;

        bgShifterAttributeLow_8 = (bgShifterAttributeLow_8 & 0xFF00) | (((bgNextTileAttribute & 0b01) > 0) ? 0xFF : 0x00);
        bgShifterAttributeHigh_8 = (bgShifterAttributeHigh_8 & 0xFF00) | (((bgNextTileAttribute & 0b10) > 0) ? 0xFF : 0x00);
    }

    private void updateShifters() {
        if (maskRegister_8.renderBackground_1 > 0) {
            bgShifterPatternLow_8 <<= 1;
            bgShifterPatternHigh_8 <<= 1;

            bgShifterAttributeLow_8 <<= 1;
            bgShifterAttributeHigh_8 <<= 1;
        }
        if (maskRegister_8.renderSprites_1 > 0 && cycle >= 1 && cycle < 258) {
            for (int i = 0; i < spriteCount; i++) {
                if (spriteScanlines[i].x_8 > 0) {
                    spriteScanlines[i].x_8--;
                } else {
                    spriteShifterPatternLow[i] <<= 1;
                    spriteShifterPatternHigh[i] <<= 1;
                }
            }
        }
    }

    private void incrementScrollX() {
        if (maskRegister_8.renderBackground_1 > 0 || maskRegister_8.renderSprites_1 > 0) {
            if (vRam_16.coarseX_5 == 31) {
                vRam_16.coarseX_5 = 0;
                vRam_16.nametableX_1 = vRam_16.nametableX_1 > 0 ? 0 : 1;
            } else {
                vRam_16.coarseX_5++;
            }
        }
    }

    private void incrementScrollY() {
        if (maskRegister_8.renderBackground_1 > 0 || maskRegister_8.renderSprites_1 > 0) {
            if (vRam_16.fineY_3 < 7) {
                vRam_16.fineY_3++;
            } else {
                vRam_16.fineY_3 = 0;
                if (vRam_16.coarseY_5 == 29) {
                    vRam_16.coarseY_5 = 0;
                    vRam_16.nametableY_1 = vRam_16.nametableY_1 > 0 ? 0 : 1;
                } else if (vRam_16.coarseY_5 == 31) {
                    vRam_16.coarseY_5 = 0;
                } else {
                    vRam_16.coarseY_5++;
                }
            }
        }
    }

    private void transferAddressX() {
        if (maskRegister_8.renderBackground_1 > 0 || maskRegister_8.renderSprites_1 > 0) {
            vRam_16.nametableX_1 = tRam_16.nametableX_1;
            vRam_16.coarseX_5 = tRam_16.coarseX_5;
        }
    }

    private void transferAddressY() {
        if (maskRegister_8.renderBackground_1 > 0 || maskRegister_8.renderSprites_1 > 0) {
            vRam_16.fineY_3 = tRam_16.fineY_3;
            vRam_16.nametableY_1 = tRam_16.nametableY_1;
            vRam_16.coarseY_5 = tRam_16.coarseY_5;
        }
    }

    public void connectCartridge(Cartridge cartridge) {
        this.cartridge = cartridge;
    }
}
