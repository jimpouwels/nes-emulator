public class Main {

    private static final int MEMORY_SIZE_IN_KILOBYTES = 64;
    private static final int NUMBER_OF_BYTES_IN_KILOBYTE = 1024;

    public static void main(String... args) {
        System.out.println("Starting Jim's NES Emulator!");
        System.out.println("-----------------------------");
        System.out.println("MemorySize: " + MEMORY_SIZE_IN_KILOBYTES + "kb");
        Olc6502 cpu = new Olc6502(new Bus(MEMORY_SIZE_IN_KILOBYTES * NUMBER_OF_BYTES_IN_KILOBYTE));

    }
}
