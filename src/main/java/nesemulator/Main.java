package nesemulator;

import nesemulator.cpu.Olc6502;
import nesemulator.cpu.Operation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    private static final int MEMORY_SIZE_IN_KILOBYTES = 64;
    private static final int NUMBER_OF_BYTES_IN_KILOBYTE = 1024;

    public static void main(String... args) {
        System.out.println("Starting Jim's NES Emulator!");
        System.out.println("-----------------------------");
        System.out.println("MemorySize: " + MEMORY_SIZE_IN_KILOBYTES + "kb");
        Bus bus = new Bus(MEMORY_SIZE_IN_KILOBYTES * NUMBER_OF_BYTES_IN_KILOBYTE);
        Olc6502 cpu = null;
        try {
            cpu = new Olc6502(bus, Files.readAllBytes(Paths.get("./nestest.nes")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        printInstructionSet(cpu.getInstructions());
        System.out.println("Starting testrom...");
        cpu.start();
    }

    private static void printInstructionSet(Operation[] operations) {
        System.out.println("\nInstructionSet");
        System.out.println("-----------------------------");
        for (int i = 0; i < operations.length; i += 16) {
            for (int y = i; y < i + 16; y++) {
                System.out.print(operations[y].instruction + "   ");
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
