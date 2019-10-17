package nl.pouwels.nes.utils;

public class PrintUtilities {
    public static String printAsHex(int value, int charCount) {
        return String.format("%0" + charCount + "x", value).toUpperCase();
    }
}
