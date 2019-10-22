package nl.pouwels.nes.mappers;

public class Mapper3 extends Mapper {

    public Mapper3(int nrOfProgramBanks, int nrOfCharacterBanks) {
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
        return 0x1FFF;
    }

    @Override
    public int mapToProgramROMAddress(int address_16, int bankRegister_8) {
        return address_16 & (nrOfProgramBanks > 1 ? 0x7FFF : 0x3FFF);
    }

    @Override
    public int mapToCharacterROMAddress(int address_16, int bankRegister_8) {
        return address_16 + (bankRegister_8 * 8192);
    }

}
