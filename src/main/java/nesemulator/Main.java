package nesemulator;

import nesemulator.cartridge.Cartridge;
import nesemulator.cpu.Olc6502;
import nesemulator.cpu.Operation;
import nesemulator.ppu.Olc2c02;

public class Main {

    private static final int MEMORY_SIZE_IN_KILOBYTES = 64;

    public static void main(String... args) {
        System.out.println("Starting Jim's NES Emulator!");
        System.out.println("-----------------------------");
        Cartridge cartridge = new Cartridge("./nestest.nes");
        Olc6502 cpu = new Olc6502();
        Bus bus = new Bus(cpu, new Olc2c02());
        cpu.connectToBus(bus);
        bus.insertCartridge(cartridge);
        System.out.println("Starting testrom...");
        bus.reset();
        while (true) {
            bus.clock();
        }
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
