package nl.pouwels.nes.mappers;

/**
 * The pattern memory is 8kb
 */
public class Mapper2 extends Mapper {

    public Mapper2(int nrOfProgramBanks, int nrOfCharacterBanks) {
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
        if (address_16 >= 0xC000) {
            return (address_16 - 0xC000) + (0x4000 * (nrOfProgramBanks - 1));
        }
        return (address_16 - 0x8000) + (0x4000 * bankRegister_8);
    }

    @Override
    public int mapToCharacterROMAddress(int address_16, int bankRegister_8) {
        return address_16;
    }

}
