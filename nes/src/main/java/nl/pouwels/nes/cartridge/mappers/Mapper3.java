package nl.pouwels.nes.cartridge.mappers;

import nl.pouwels.nes.cartridge.registers.BankSelectRegister;

public class Mapper3 extends Mapper {

    private int nrOfProgramBanks;
    private BankSelectRegister bankSelectRegister;

    public Mapper3(int nrOfProgramBanks, BankSelectRegister bankSelectRegister) {
        this.nrOfProgramBanks = nrOfProgramBanks;
        this.bankSelectRegister = bankSelectRegister;
    }

    @Override
    public int mapToProgramROMAddress(int address_16) {
        return address_16 & (nrOfProgramBanks > 1 ? 0x7FFF : 0x3FFF);
    }

    @Override
    public int mapToCharacterROMAddress(int address_16) {
        return address_16 + (bankSelectRegister.getBankSelectRegister() * 8192);
    }

    @Override
    public void scanlineSignal() {

    }

}
