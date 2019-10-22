package nl.pouwels.nes.cartridge;

import nl.pouwels.nes.cartridge.mappers.Mapper;
import nl.pouwels.nes.cartridge.registers.MMC3Registers;
import nl.pouwels.nes.ppu.NametableMirroringMode;

public class TxROMCartridge extends Cartridge {
    private MMC3Registers mmc3Registers;

    public TxROMCartridge(Mapper mapper, int[] programMemory, int[] characterMemory, NametableMirroringMode nametableMirroringMode, MMC3Registers mmc3Registers) {
        super(mapper, programMemory, characterMemory, nametableMirroringMode);
        this.mmc3Registers = mmc3Registers;
    }

    @Override
    public void cpuWriteByte(int address_16, int data_8) {

    }

    @Override
    public int cpuReadByte(int address_16) {
        return 0;
    }

    @Override
    public int ppuReadByte(int address_16) {
        return 0;
    }

    @Override
    public void ppuWriteByte(int address_16, int data_8) {

    }
}
