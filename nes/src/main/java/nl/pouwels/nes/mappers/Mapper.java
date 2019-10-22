package nl.pouwels.nes.mappers;

public abstract class Mapper {

    protected int nrOfProgramBanks;
    protected int nrOfCharacterBanks;

    Mapper(int nrOfProgramBanks, int nrOfCharacterBanks) {
        this.nrOfProgramBanks = nrOfProgramBanks;
        this.nrOfCharacterBanks = nrOfCharacterBanks;
    }

    public abstract int getProgramROMRangeStart();

    public abstract int getProgramROMRangeEnd();

    public abstract int getCharacterROMRangeStart();

    public abstract int getCharacterROMRangeEnd();

    public abstract int mapToProgramROMAddress(int address_16);

    public abstract int mapToCharacterROMAddress(int address_16, int bankRegister);

}
