package nl.pouwels.nes.cartridge;

import nl.pouwels.nes.cartridge.mappers.Mapper;
import nl.pouwels.nes.cartridge.registers.MMC3Registers;
import nl.pouwels.nes.ppu.NametableMirroringMode;

public class TxROMCartridge extends Cartridge {
    private MMC3Registers mmc3Registers;
    private boolean isUpdatingBankRegister = false;

    public TxROMCartridge(Mapper mapper, int[] programMemory, int[] characterMemory, NametableMirroringMode nametableMirroringMode, MMC3Registers mmc3Registers) {
        super(mapper, programMemory, characterMemory, nametableMirroringMode);
        this.mmc3Registers = mmc3Registers;
    }

    @Override
    public void cpuWriteByte(int address_16, int data_8) {
        if (address_16 >= 0x8000 && address_16 <= 0x9FFE && (address_16 % 2 == 0)) {
            mmc3Registers.bankSelect_8 = data_8;
        } else if (address_16 >= 0x8001 && address_16 <= 0x9FFF && (address_16 % 2 != 0)) {
            if ((data_8 & 0x07) == 0x00 || (data_8 & 0x07) == 0x01) {
                data_8 &= ~0x01;
            }
            if ((data_8 & 0x07) == 0x06 || (data_8 & 0x07) == 0x07) {
                data_8 &= ~0xC0;
            }
            mmc3Registers.bankData_8 = data_8;
            mmc3Registers.bankRegisters[mmc3Registers.bankSelect_8 & 0x07] = data_8;
        } else if (address_16 >= 0xA000 && address_16 <= 0xBFFE && (address_16 % 2 == 0)) {
            mmc3Registers.mirroring_8 = data_8;
        } else if (address_16 >= 0xA001 && address_16 <= 0xBFFF && (address_16 % 2 != 0)) {
            mmc3Registers.pgrRamProtect_8 = data_8;
        } else if (address_16 >= 0xC000 && address_16 <= 0xDFFE && (address_16 % 2 == 0)) {
            mmc3Registers.irqLatch_8 = data_8;
        } else if (address_16 >= 0xC001 && address_16 <= 0xDFFF && (address_16 % 2 != 0)) {
            mmc3Registers.irqReload_8 = data_8;
        } else if (address_16 >= 0xE000 && address_16 <= 0xFFFE && (address_16 % 2 == 0)) {
            mmc3Registers.irqDisable_8 = data_8;
        } else if (address_16 >= 0xE001 && address_16 <= 0xFFFF && (address_16 % 2 != 0)) {
            mmc3Registers.irqEnable_8 = data_8;
        }
    }

    @Override
    public int cpuReadByte(int address_16) {
        return programMemory[mapper.mapToProgramROMAddress(address_16)];
    }

    @Override
    public int ppuReadByte(int address_16) {
        return characterMemory[mapper.mapToCharacterROMAddress(address_16)];
    }

    @Override
    public void ppuWriteByte(int address_16, int data_8) {

    }
}
