package nl.pouwels.nes.cartridge.mappers;

import nl.pouwels.nes.cartridge.registers.BankSelectRegister;
import nl.pouwels.nes.cartridge.registers.MMC3Registers;

public class Mapper4 extends Mapper {
    public Mapper4(BankSelectRegister bankSelectRegister, MMC3Registers mmc3Registers) {

    }

    @Override
    public int mapToProgramROMAddress(int address_16) {
        return 0;
    }

    @Override
    public int mapToCharacterROMAddress(int address_16) {
        return 0;
    }
}
