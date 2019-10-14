package nesemulator.utils;

public class ByteUtilities {

    public static int widenIgnoreSigning(byte byteToWiden) {
        return byteToWiden & 0xFF;
    }

    public static int widenIgnoreSigning(short shortToWiden) {
        return (int) shortToWiden & 0xFFFF;
    }

    public static int unsetBit(int value, int bitNumber) {
        return value & ~(1 << bitNumber);
    }

    public static boolean isBitSet(int value, int bitNumber) {
        return (value & (1 << bitNumber)) > 0;
    }
}
