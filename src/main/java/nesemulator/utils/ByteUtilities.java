package nesemulator.utils;

public class ByteUtilities {

    public static int widenIgnoreSigning(byte byteToWiden) {
        return byteToWiden & 0xFF;
    }

    public static int widenIgnoreSigning(short shortToWiden) {
        return (int) shortToWiden & 0xFFFF;
    }
}
