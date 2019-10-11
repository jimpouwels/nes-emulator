package nesemulator.cartridge;

import static nesemulator.utils.ByteUtilities.widenIgnoreSigning;

public class ByteReader {

    private final byte[] bytes;
    private int bytePointer;

    public ByteReader(byte[] bytes) {
        this.bytes = bytes;
    }

    public int readNextByte() {
        return widenIgnoreSigning(bytes[bytePointer++]);
    }

    public int[] readNextBytes(int count) {
        int[] bytesRead = new int[count];
        for (int i = 0; i < count; i++) {
            bytesRead[i] = readNextByte();
        }
        return bytesRead;
    }

    public String readNextString(int charCount) {
        byte[] readBytes = new byte[charCount];
        for (int i = 0; i < charCount; i++) {
            readBytes[i] = bytes[bytePointer++];
        }
        return new String(readBytes);
    }

    public void skipBytes(int count) {
        bytePointer += count;
    }
}
