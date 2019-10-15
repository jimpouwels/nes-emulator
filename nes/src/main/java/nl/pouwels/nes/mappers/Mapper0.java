package nl.pouwels.nes.mappers;

public class Mapper0 extends Mapper {

    public Mapper0(int nrOfProgramBanks, int nrOfCharacterBanks) {
        super(nrOfProgramBanks, nrOfCharacterBanks);
    }

    @Override
    public int getProgramROMRangeStart() {
        return 0x8000;
    }

    @Override
    public int getProgramROMRangeEnd() {
        return 0xFFFF;
    }

    @Override
    public int getCharacterROMRangeStart() {
        return 0x0000;
    }

    @Override
    public int getCharacterROMRangeEnd() {
        return 0x1FFFF;
    }

    @Override
    public int mapToProgramROMAddress(int address_16) {
        return address_16 & (nrOfProgramBanks > 1 ? 0x7FFF : 0x3FFF);
    }

    @Override
    public int mapToCharacterROMAddress(int address_16) {
        return address_16;
    }

}
