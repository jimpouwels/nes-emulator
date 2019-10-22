package nl.pouwels.nes.cartridge.mappers;

public abstract class Mapper {

    protected int nrOfProgramBanks;
    protected int nrOfCharacterBanks;

    public Mapper(int nrOfProgramBanks, int nrOfCharacterBanks) {
        this.nrOfProgramBanks = nrOfProgramBanks;
        this.nrOfCharacterBanks = nrOfCharacterBanks;
    }

    public abstract int mapToProgramROMAddress(int address_16);

    public abstract int mapToCharacterROMAddress(int address_16);

}
