package nl.pouwels.nes.cartridge.mappers;

import nl.pouwels.nes.Bus;
import nl.pouwels.nes.cartridge.registers.MMC3Registers;

public class Mapper4 extends Mapper {

    private MMC3Registers mmc3Registers;

    private int[] characterBankPointer = new int[8];
    private int[] pgrMappings = new int[4];
    private final int pgrBankCount;
    public int irqCounter;
    private Bus nes;

    public Mapper4(MMC3Registers mmc3Registers, int programMemorySizeInBytes, Bus nes) {
        this.mmc3Registers = mmc3Registers;
        this.pgrBankCount = programMemorySizeInBytes / 0x2000;
        this.nes = nes;
    }

    @Override
    public int mapToProgramROMAddress(int address_16) {
        int prgMode = (mmc3Registers.bankSelect_8 >> 6) & 0x01;

        if (prgMode == 0x00) {
            pgrMappings[0] = mmc3Registers.bankRegisters[6];
            pgrMappings[1] = mmc3Registers.bankRegisters[7];
            pgrMappings[2] = pgrBankCount - 2;
            pgrMappings[3] = pgrBankCount - 1;
        } else {
            pgrMappings[0] = pgrBankCount - 2;
            pgrMappings[1] = mmc3Registers.bankRegisters[7];
            pgrMappings[2] = mmc3Registers.bankRegisters[6];
            pgrMappings[3] = pgrBankCount - 1;
        }
        int bank = (address_16 - 0x8000) / 0x2000;
        int rest = (address_16 - 0x8000) % 0x2000;
        int mappedBank = pgrMappings[bank];
        return (mappedBank) * 0x2000 + rest;
    }

    @Override
    public int mapToCharacterROMAddress(int address_16) {
        int chrMode = (mmc3Registers.bankSelect_8 >> 7) & 0x01;

        if (chrMode == 0x00) {
            characterBankPointer[0] = mmc3Registers.bankRegisters[0] & 0xfe;
            characterBankPointer[1] = mmc3Registers.bankRegisters[0] | 0x01;
            characterBankPointer[2] = mmc3Registers.bankRegisters[1] & 0xfe;
            characterBankPointer[3] = mmc3Registers.bankRegisters[1] | 0x01;
            characterBankPointer[4] = mmc3Registers.bankRegisters[2];
            characterBankPointer[5] = mmc3Registers.bankRegisters[3];
            characterBankPointer[6] = mmc3Registers.bankRegisters[4];
            characterBankPointer[7] = mmc3Registers.bankRegisters[5];

        } else {
            characterBankPointer[0] = mmc3Registers.bankRegisters[2];
            characterBankPointer[1] = mmc3Registers.bankRegisters[3];
            characterBankPointer[2] = mmc3Registers.bankRegisters[4];
            characterBankPointer[3] = mmc3Registers.bankRegisters[5];
            characterBankPointer[4] = mmc3Registers.bankRegisters[0] & 0xfe;
            characterBankPointer[5] = mmc3Registers.bankRegisters[0] | 0x01;
            characterBankPointer[6] = mmc3Registers.bankRegisters[1] & 0xfe;
            characterBankPointer[7] = mmc3Registers.bankRegisters[1] | 0x01;
        }
        int bank = address_16 / 0x0400;
        int rest = address_16 % 0x0400;
        int mappedBank = characterBankPointer[bank];
        return mappedBank * 0x0400 + rest;
    }

    @Override
    public void scanlineSignal() {
        irqCounter--;
        if (irqCounter == 0 && mmc3Registers.irqEnable) {
            irqCounter = mmc3Registers.irqLatch_8;
            nes.irq();
        }
        if (mmc3Registers.irqReload_8 > 0) {
            irqCounter = mmc3Registers.irqLatch_8;
            mmc3Registers.irqReload_8 = 0;
        }

    }

}
