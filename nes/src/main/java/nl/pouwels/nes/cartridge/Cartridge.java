package nl.pouwels.nes.cartridge;

import nl.pouwels.nes.mappers.Mapper;
import nl.pouwels.nes.mappers.Mapper0;
import nl.pouwels.nes.mappers.Mapper2;
import nl.pouwels.nes.mappers.Mapper3;
import nl.pouwels.nes.ppu.NametableMirroringMode;
import nl.pouwels.nes.utils.ByteUtilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Cartridge {

    public NametableMirroringMode nametableMirroringMode;
    private Mapper mapper;
    private int[] programMemory;
    private int[] characterMemory;
    private INesFormatHeader header = new INesFormatHeader();
    private int mapperId;
    private int nrOfProgramBanks;
    private int nrOfCharacterBanks;
    private int bankRegister_8;

    public Cartridge(String filename) {
        loadCartridge(filename);
    }

    private void loadCartridge(String filename) {
        try {
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
            mapperId = ((header.mapper2 >> 4) << 4) | (header.mapper1 >> 4);
            nametableMirroringMode = (header.mapper1 & 0x01) > 0 ? NametableMirroringMode.VERTICAL : NametableMirroringMode.HORIZONTAL;

            nrOfProgramBanks = header.programRomChunks;
            programMemory = new int[nrOfProgramBanks * 16384];
            programMemory = reader.readNextBytes(programMemory.length);

            nrOfCharacterBanks = header.characterRomChunks;
            characterMemory = reader.readNextBytes(nrOfCharacterBanks * 8192);
            if (characterMemory.length == 0) {
                characterMemory = new int[8192];
            }

            switch (mapperId) {
                case 0:
                    mapper = new Mapper0(nrOfProgramBanks, nrOfCharacterBanks);
                    break;
                case 2:
                    mapper = new Mapper2(nrOfProgramBanks, nrOfCharacterBanks);
                    break;
                case 3:
                    mapper = new Mapper3(nrOfProgramBanks, nrOfCharacterBanks);
                    break;
                default:
                    throw new RuntimeException("Unsupported mapper " + mapperId);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading ROM '" + filename + "'", e);
        }
    }

    public boolean isInProgramRomRange(int address_16) {
        return address_16 >= mapper.getProgramROMRangeStart() && address_16 <= mapper.getProgramROMRangeEnd();
    }

    public boolean isInCharacterRomRange(int address_16) {
        return address_16 >= mapper.getCharacterROMRangeStart() && address_16 <= mapper.getCharacterROMRangeEnd();
    }

    public int cpuReadByte(int address_16) {
        if (isInProgramRomRange(address_16)) {
            return programMemory[mapper.mapToProgramROMAddress(address_16, bankRegister_8)];
        }
        throw new RuntimeException("Address " + "%x" + address_16 + " is outside program ROM range");
    }

    public void cpuWriteByte(int address_16, int data_8) {
        if (isInProgramRomRange(address_16)) {
            bankRegister_8 = data_8;
        } else {
            throw new RuntimeException("Address " + "%x" + address_16 + " is outside program ROM range");
        }
    }

    public int ppuReadByte(int address_16) {
        if (isInCharacterRomRange(address_16)) {
            return characterMemory[mapper.mapToCharacterROMAddress(address_16, bankRegister_8)];
        }
        throw new RuntimeException("Address " + "%x" + address_16 + " is outside Character ROM range");
    }

    public void ppuWriteByte(int address_16, int data_8) {
        if (isInCharacterRomRange(address_16)) {
            characterMemory[mapper.mapToCharacterROMAddress(address_16, bankRegister_8)] = data_8;
        } else {
            throw new RuntimeException("Address " + "%x" + address_16 + " is outside Character ROM range");
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
