package nesemulator.utils;

public class ByteUtilities {

    public static short sumAsUnsignedAndWiden(byte byte1, byte byte2) {
        short byte1AsUnsighed = widenAsUnsigned(byte1);
        short byte2AsUnsighed = widenAsUnsigned(byte2);
        return (short) (byte1AsUnsighed + byte2AsUnsighed);
    }

    public static short widenAsUnsigned(byte byteToWiden) {
        return (short) ((short) byteToWiden & 0xFF);
    }
}
