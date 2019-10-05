package nesemulator.utils;

public class ByteUtilities {

    public static short widenIgnoreSigning(byte byteToWiden) {
        return (short) ((short) byteToWiden & 0xFF);
    }
}
