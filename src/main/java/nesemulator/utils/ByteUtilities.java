package nesemulator.utils;

public class ByteUtilities {

    public static short widenIgnoreSigning(byte byteToWiden) {
        return (short) ((short) byteToWiden & 0xFF);
    }

    public static int widenIgnoreSigning(short shortToWiden) {
        return (int) shortToWiden & 0xFFFF;
    }
}
