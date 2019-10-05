package nesemulator.utils;

public class ByteUtilities {

    public static short sumAsUnsignedAndWiden(byte byte1, byte byte2) {
        short byte1AsUnsighed = (short) ((short) byte1 & 0xFF);
        short byte2AsUnsighed = (short) ((short) byte2 & 0xFF);
        return (short) (byte1AsUnsighed + byte2AsUnsighed);
    }
}
