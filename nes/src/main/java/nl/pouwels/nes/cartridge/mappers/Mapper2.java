package nl.pouwels.nes.cartridge.mappers;

import nl.pouwels.nes.cartridge.registers.BankSelectRegister;

public class Mapper2 extends Mapper {

    private int nrOfProgramBanks;
    private BankSelectRegister bankSelectRegister;

    public Mapper2(int nrOfProgramBanks, BankSelectRegister bankselectRegister) {
        this.nrOfProgramBanks = nrOfProgramBanks;
        this.bankSelectRegister = bankselectRegister;
    }

    @Override
    public int mapToProgramROMAddress(int address_16) {
        if (address_16 >= 0xC000) {
            return (address_16 - 0xC000) + (0x4000 * (nrOfProgramBanks - 1));
        }
        return (address_16 - 0x8000) + (0x4000 * bankSelectRegister.getBankSelectRegister());
    }

    @Override
    public int mapToCharacterROMAddress(int address_16) {
        return address_16;
    }

}
