package nesemulator;

import nesemulator.cpu.Operation;
import nesemulator.cpu.Olc6502;

public class Main {

    private static final int MEMORY_SIZE_IN_KILOBYTES = 64;
    private static final int NUMBER_OF_BYTES_IN_KILOBYTE = 1024;

    public static void main(String... args) {
        System.out.println("Starting Jim's NES Emulator!");
        System.out.println("-----------------------------");
        System.out.println("MemorySize: " + MEMORY_SIZE_IN_KILOBYTES + "kb");
        Olc6502 cpu = new Olc6502(new Bus(MEMORY_SIZE_IN_KILOBYTES * NUMBER_OF_BYTES_IN_KILOBYTE));
        printInstructionSet(cpu.getInstructions());
    }

    private static void printInstructionSet(Operation[] operations) {
        System.out.println("\nInstructionSet");
        System.out.println("-----------------------------");
        for (int i = 0; i < operations.length; i += 16) {
            for (int y = i; y < i + 16; y++) {
                System.out.print(operations[y].opcode + "   ");
            }
            System.out.println();
            for (int y = i; y < i + 16; y++) {
                System.out.print(operations[y].addressingMode + "   ");
            }
            System.out.println();
            for (int y = i; y < i + 16; y++) {
                System.out.print(operations[y].cycles + "     ");
            }
            System.out.print(System.lineSeparator());
            System.out.print(System.lineSeparator());
        }
    }

}
