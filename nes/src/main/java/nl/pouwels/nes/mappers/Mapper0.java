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
        return 0x1FFF;
    }

    @Override
    public int mapToProgramROMAddress(int address_16, int bankRegister_8) {
        // mask to 0x7FFF if 32Kb, mask with 0x3FFF in case of 16Kb
        return address_16 & (nrOfProgramBanks > 1 ? address_16 - 0x8000 : address_16 - 0xC000);
    }

    @Override
    public int mapToCharacterROMAddress(int address_16, int bankRegister_8) {
        return address_16;
    }

}
