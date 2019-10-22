package nl.pouwels.nes.cartridge.mappers;

public class Mapper0 extends Mapper {

    public Mapper0(int nrOfProgramBanks, int nrOfCharacterBanks) {
        super(nrOfProgramBanks, nrOfCharacterBanks);
    }

    @Override
    public int mapToProgramROMAddress(int address_16) {
        return address_16 & (nrOfProgramBanks > 1 ? address_16 - 0x8000 : address_16 - 0xC000);
    }

    @Override
    public int mapToCharacterROMAddress(int address_16) {
        return address_16;
    }

}
