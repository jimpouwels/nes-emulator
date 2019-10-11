package nesemulator.mappers;

public class Mapper0 extends Mapper {

    public Mapper0(int nrOfProgramBanks, int nrOfCharacterBanks) {
        super(nrOfProgramBanks, nrOfCharacterBanks);
    }

    @Override
    public int cpuMapRead(int address_16) {
        if (address_16 >= 0x8000 && address_16 <= 0xFFFF) {
            return address_16 & (nrOfProgramBanks > 1 ? 0x7FFF : 0x3FFF);
        }
        return -1;
    }

    @Override
    public int cpuMapWrite(int address_16) {
        if (address_16 >= 0x8000 && address_16 <= 0xFFFF) {
            return address_16 & (nrOfProgramBanks > 1 ? 0x7FFF : 0x3FFF);
        }
        return -1;
    }

    @Override
    public int ppuMapRead(int address_16) {
        if (address_16 >= 0x0000 && address_16 <= 0x1FFFF) {
            return address_16;
        }
        return -1;
    }

    @Override
    public int ppuMapWrite(int address_16) {
        if (address_16 >= 0x0000 && address_16 <= 0x1FFFF) {
            if (nrOfCharacterBanks == 0) {
                return address_16;
            }
        }
        return -1;
    }
}
