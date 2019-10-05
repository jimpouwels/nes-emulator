package nesemulator.utils;

public class ByteUtilities {

    public static short sumUnsignedAndWiden(byte byte1, byte byte2) {
        return (short) ((((short) byte1) & 0xFF) & (((short) byte2) & 0xFF));
    }
}
