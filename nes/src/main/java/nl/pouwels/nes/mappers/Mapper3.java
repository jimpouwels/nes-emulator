package nl.pouwels.nes.mappers;

/**
 * The pattern memory is 8kb
 */
public class Mapper3 extends Mapper {

    private int nrOfProgramBanks;

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
        // mask to 0x7FFF if 32Kb, mask with 0x3FFF in case of 16Kb
        return address_16 & (nrOfProgramBanks > 1 ? 0x7FFF : 0x3FFF);
    }

    @Override
    public int mapToCharacterROMAddress(int address_16, int bankRegister_8) {
        return address_16 + (bankRegister_8 * 8192);
    }

}
