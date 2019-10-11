package nesemulator.cartridge;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Cartridge {

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
