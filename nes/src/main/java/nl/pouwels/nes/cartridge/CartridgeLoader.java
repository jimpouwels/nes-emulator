package nl.pouwels.nes.cartridge;

import nl.pouwels.nes.cartridge.mappers.Mapper0;
import nl.pouwels.nes.cartridge.mappers.Mapper2;
import nl.pouwels.nes.cartridge.mappers.Mapper3;
import nl.pouwels.nes.cartridge.mappers.Mapper4;
import nl.pouwels.nes.cartridge.registers.BankSelectRegister;
import nl.pouwels.nes.cartridge.registers.MMC3Registers;
import nl.pouwels.nes.ppu.NametableMirroringMode;
import nl.pouwels.nes.utils.ByteUtilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CartridgeLoader {
    public static Cartridge loadCartridge(String filename) {
        try {
            INesFormatHeader header = new INesFormatHeader();

            ByteReader reader = new ByteReader(Files.readAllBytes(Paths.get(filename)));
            header.name = reader.readNextString(4);
            header.programRomChunks = reader.readNextByte();
            header.characterRomChunks = reader.readNextByte();
            header.mapper1 = reader.readNextByte();
            header.mapper2 = reader.readNextByte();
            header.programRamSize = reader.readNextByte();
            header.tvSystem1 = reader.readNextByte();
            header.tvSystem2 = reader.readNextByte();
            header.unused = reader.readNextString(5);
            if (ByteUtilities.isBitSet(header.mapper1, 3)) {
                reader.skipBytes(512);
            }
            int mapperId = ((header.mapper2 >> 4) << 4) | (header.mapper1 >> 4);
            NametableMirroringMode nametableMirroringMode = (header.mapper1 & 0x01) > 0 ? NametableMirroringMode.VERTICAL : NametableMirroringMode.HORIZONTAL;

            int nrOfProgramBanks = header.programRomChunks;
            int[] programMemory = new int[nrOfProgramBanks * 16384];
            programMemory = reader.readNextBytes(programMemory.length);

            int nrOfCharacterBanks = header.characterRomChunks;
            int[] characterMemory = reader.readNextBytes(nrOfCharacterBanks * 8192);
            if (characterMemory.length == 0) {
                characterMemory = new int[8192];
            }

            switch (mapperId) {
                case 0:
                    return new NROMCartridge(new Mapper0(nrOfProgramBanks), programMemory, characterMemory, nametableMirroringMode);
                case 2:
                    BankSelectRegister bankSelectRegisterMapper2 = new BankSelectRegister();
                    return new UROMCartridge(new Mapper2(nrOfProgramBanks, bankSelectRegisterMapper2), programMemory, characterMemory, nametableMirroringMode, bankSelectRegisterMapper2);
                case 3:
                    BankSelectRegister bankSelectRegisterMapper3 = new BankSelectRegister();
                    return new CNRomCartidge(new Mapper3(nrOfProgramBanks, bankSelectRegisterMapper3), programMemory, characterMemory, nametableMirroringMode, bankSelectRegisterMapper3);
                case 4:
                    BankSelectRegister bankSelectRegisterMapper4 = new BankSelectRegister();
                    MMC3Registers mmc3Registers = new MMC3Registers();
                    return new TxROMCartridge(new Mapper4(bankSelectRegisterMapper4, mmc3Registers), programMemory, characterMemory, nametableMirroringMode, mmc3Registers);
                default:
                    throw new RuntimeException("Unsupported mapper " + mapperId);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading ROM '" + filename + "'", e);
        }
    }

    private static class INesFormatHeader {
        String name;
        int programRomChunks;
        int characterRomChunks;
        int mapper1;
        int mapper2;
        int programRamSize;
        int tvSystem1;
        int tvSystem2;
        String unused;
    }
}
