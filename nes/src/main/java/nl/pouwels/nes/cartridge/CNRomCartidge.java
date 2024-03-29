package nl.pouwels.nes.cartridge;

import nl.pouwels.nes.cartridge.mappers.Mapper;
import nl.pouwels.nes.cartridge.registers.BankSelectRegister;
import nl.pouwels.nes.ppu.NametableMirroringMode;

public class CNRomCartidge extends Cartridge {
    private BankSelectRegister bankSelectRegister;

    public CNRomCartidge(Mapper mapper, int[] programMemory, int[] characterMemory, NametableMirroringMode nametableMirroringMode, BankSelectRegister bankSelectRegister, TvSystem tvSystem) {
        super(mapper, programMemory, characterMemory, nametableMirroringMode, tvSystem);
        this.bankSelectRegister = bankSelectRegister;
    }

    @Override
    public void cpuWriteByte(int address_16, int data_8) {
        bankSelectRegister.setBankSelectRegister(data_8);
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
        characterMemory[mapper.mapToCharacterROMAddress(address_16)] = data_8;
    }

    @Override
    public void cpuWriteByteToRam(int address_16, int data_8) {

    }

    @Override
    public int cpuReadByteFromRam(int address_16) {
        return 0;
    }

}
