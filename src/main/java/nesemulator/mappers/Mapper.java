package nesemulator.mappers;

public abstract class Mapper {

    protected int nrOfProgramBanks;
    protected int nrOfCharacterBanks;

    Mapper(int nrOfProgramBanks, int nrOfCharacterBanks) {
        this.nrOfProgramBanks = nrOfProgramBanks;
        this.nrOfCharacterBanks = nrOfCharacterBanks;
    }

    public abstract int cpuMapRead(int address_16);

    public abstract int cpuMapWrite(int address_16);

    public abstract int ppuMapRead(int address_16);

    public abstract int ppuMapWrite(int address_16);
}
