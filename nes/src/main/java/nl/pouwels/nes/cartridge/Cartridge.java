package nl.pouwels.nes.cartridge;

import nl.pouwels.nes.cartridge.mappers.Mapper;
import nl.pouwels.nes.ppu.NametableMirroringMode;

public abstract class Cartridge {

    public NametableMirroringMode nametableMirroringMode;
    public Mapper mapper;
    int[] programMemory;
    int[] characterMemory;

    public Cartridge(Mapper mapper, int[] programMemory, int[] characterMemory, NametableMirroringMode nametableMirroringMode, TvSystem tvSystem) {
        this.mapper = mapper;
        this.programMemory = programMemory;
        this.characterMemory = characterMemory;
        this.nametableMirroringMode = nametableMirroringMode;
    }

    public boolean isInProgramRomRange(int address_16) {
        return address_16 >= 0x8000 && address_16 <= 0xFFFF;
    }

    public boolean isInProgramRamRange(int address_16) {
        return address_16 >= 0x6000 && address_16 <= 0x7FFF;
    }

    public boolean isInCharacterRomRange(int address_16) {
        return address_16 >= 0x0000 && address_16 <= 0x1FFF;
    }

    public abstract void cpuWriteByte(int address_16, int data_8);

    public abstract int cpuReadByte(int address_16);

    public abstract int ppuReadByte(int address_16);

    public abstract void ppuWriteByte(int address_16, int data_8);

    public abstract void cpuWriteByteToRam(int address_16, int data_8);

    public abstract int cpuReadByteFromRam(int address_16);
}
