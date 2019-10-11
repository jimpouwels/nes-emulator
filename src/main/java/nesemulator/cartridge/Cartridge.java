package nesemulator.cartridge;

import nesemulator.mappers.Mapper;
import nesemulator.mappers.Mapper0;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Cartridge {

    private Mapper mapper;
    private int[] programMemory;
    private int[] characterMemory;
    private INesFormatHeader header = new INesFormatHeader();
    private int mapperId;
    private int nrOfProgramBanks;
    private int nrOfCharacterBanks;

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
            if ((header.mapper1 & 0x04) > 0) {
                reader.skipBytes(512);
            }
            mapperId = ((header.mapper2 >> 4) << 4) | (header.mapper1 >> 4);

            // FileType == 1
            nrOfProgramBanks = header.programRomChunks;
            programMemory = new int[16384];
            programMemory = reader.readNextBytes(programMemory.length);

            nrOfCharacterBanks = header.characterRomChunks;
            characterMemory = new int[8192];
            characterMemory = reader.readNextBytes(characterMemory.length);

            switch (mapperId) {
                case 0:
                    mapper = new Mapper0(nrOfProgramBanks, nrOfCharacterBanks);
                    break;
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading ROM '" + filename + "'", e);
        }
    }

    public int cpuReadByte(int address_16) {
        int data = -1;
        int mappedAddress = mapper.cpuMapRead(address_16);
        if (mappedAddress != -1) {
            data = programMemory[mappedAddress];
        }
        return data;
    }

    public boolean cpuWriteByte(int address_16, int data_8) {
        int mappedAddress = mapper.cpuMapWrite(address_16);
        if (mappedAddress != -1) {
            programMemory[mappedAddress] = data_8;
            return true;
        }
        return false;
    }

    public int ppuReadByte(int address_16) {
        int data = -1;
        int mappedAddress = mapper.ppuMapRead(address_16);
        if (mappedAddress != -1) {
            data = characterMemory[mappedAddress];
        }
        return data;
    }

    public boolean ppuWriteByte(int address_16, int data_8) {
        int mappedAddress = mapper.ppuMapWrite(address_16);
        if (mappedAddress != -1) {
            characterMemory[mappedAddress] = data_8;
            return true;
        }
        return false;
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
