package nl.pouwels.nes.cartridge.mappers;

import nl.pouwels.nes.cartridge.registers.MMC3Registers;

public class Mapper4 extends Mapper {

    private MMC3Registers mmc3Registers;
    private int nrOfProgramBanks;

    public Mapper4(MMC3Registers mmc3Registers, int nrOfProgramBanks) {
        this.mmc3Registers = mmc3Registers;
        this.nrOfProgramBanks = nrOfProgramBanks;
    }

    @Override
    public int mapToProgramROMAddress(int address_16) {
        int bankOffset = 0x0000;
        int addressMinus = 0x0000;
        if (address_16 >= 0x8000 && address_16 <= 0x9FFF) {
            if ((mmc3Registers.bankSelect_8 & 0x40) == 0) {
                bankOffset = mmc3Registers.bankRegisters[6];
            } else {
                bankOffset = nrOfProgramBanks - 3;
            }
            addressMinus = address_16 - 0x8000;
        } else if (address_16 >= 0xA000 && address_16 <= 0xBFFF) {
            if ((mmc3Registers.bankSelect_8 & 0x40) == 0) {
                bankOffset = mmc3Registers.bankRegisters[7];
            } else {
                bankOffset = mmc3Registers.bankRegisters[7];
            }
            addressMinus = address_16 - 0xA000;
        } else if (address_16 >= 0xC000 && address_16 <= 0xDFFF) {
            if ((mmc3Registers.bankSelect_8 & 0x40) == 0) {
                bankOffset = nrOfProgramBanks - 3;
            } else {
                bankOffset = mmc3Registers.bankRegisters[6];
            }
            addressMinus = address_16 - 0xC000;
        } else if (address_16 >= 0xE000 && address_16 <= 0xFFFF) {
            if ((mmc3Registers.bankSelect_8 & 0x40) == 0) {
                bankOffset = nrOfProgramBanks - 2;
            } else {
                bankOffset = nrOfProgramBanks - 2;
            }
            addressMinus = address_16 - 0xE000;
        }
        return (bankOffset * 0x2000) + (address_16 - addressMinus);
    }

    @Override
    public int mapToCharacterROMAddress(int address_16) {
        int bankOffset = 0x0000;
        if (address_16 >= 0x0000 && address_16 <= 0x03FF) {
            if ((mmc3Registers.bankSelect_8 & 0x80) == 0) {
                bankOffset = mmc3Registers.bankRegisters[0];
            } else {
                bankOffset = mmc3Registers.bankRegisters[2];
            }
        } else if (address_16 >= 0x0400 && address_16 <= 0x07FF) {
            if ((mmc3Registers.bankSelect_8 & 0x80) == 0) {
                bankOffset = mmc3Registers.bankRegisters[0];
            } else {
                bankOffset = mmc3Registers.bankRegisters[3];
            }
        } else if (address_16 >= 0x0800 && address_16 <= 0x0BFF) {
            if ((mmc3Registers.bankSelect_8 & 0x80) == 0) {
                bankOffset = mmc3Registers.bankRegisters[1];
            } else {
                bankOffset = mmc3Registers.bankRegisters[4];
            }
        } else if (address_16 >= 0x0C00 && address_16 <= 0x0FFF) {
            if ((mmc3Registers.bankSelect_8 & 0x80) == 0) {
                bankOffset = mmc3Registers.bankRegisters[1];
            } else {
                bankOffset = mmc3Registers.bankRegisters[5];
            }
        } else if (address_16 >= 0x1000 && address_16 <= 0x13FF) {
            if ((mmc3Registers.bankSelect_8 & 0x80) == 0) {
                bankOffset = mmc3Registers.bankRegisters[2];
            } else {
                bankOffset = mmc3Registers.bankRegisters[0];
            }
        } else if (address_16 >= 0x1400 && address_16 <= 0x17FF) {
            if ((mmc3Registers.bankSelect_8 & 0x80) == 0) {
                bankOffset = mmc3Registers.bankRegisters[3];
            } else {
                bankOffset = mmc3Registers.bankRegisters[0];
            }
        } else if (address_16 >= 0x1800 && address_16 <= 0x1BFF) {
            if ((mmc3Registers.bankSelect_8 & 0x80) == 0) {
                bankOffset = mmc3Registers.bankRegisters[4];
            } else {
                bankOffset = mmc3Registers.bankRegisters[1];
            }
        } else if (address_16 >= 0x1C00 && address_16 <= 0x1FFF) {
            if ((mmc3Registers.bankSelect_8 & 0x80) == 0) {
                bankOffset = mmc3Registers.bankRegisters[5];
            } else {
                bankOffset = mmc3Registers.bankRegisters[1];
            }
        }
        return (bankOffset * 0x2000) + address_16;
    }
}
