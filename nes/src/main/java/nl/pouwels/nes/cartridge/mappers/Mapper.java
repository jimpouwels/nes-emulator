package nl.pouwels.nes.cartridge.mappers;

public abstract class Mapper {

    public abstract int mapToProgramROMAddress(int address_16);

    public abstract int mapToCharacterROMAddress(int address_16);

    public abstract void scanlineSignal();
}
