package nl.pouwels.nes.main;

import nl.pouwels.nes.Bus;
import nl.pouwels.nes.Cartridge;
import nl.pouwels.nes.InstructionPrinter;
import nl.pouwels.nes.cpu.Olc6502;
import nl.pouwels.nes.ppu.Olc2c02;

public class Main {

    public static void main(String... args) {
        System.out.println("Starting Jim's NES Emulator!");
        System.out.println("-----------------------------");
        Cartridge cartridge = new Cartridge("");
        Olc6502 cpu = new Olc6502(new InstructionPrinter());
        Bus bus = new Bus(cpu, new Olc2c02());
        cpu.connectToBus(bus);
        bus.insertCartridge(cartridge);
        System.out.println("Starting testrom...");
        bus.reset();
        int i = 0;
        while (i++ < 100) {
            bus.clock();
        }
    }

}
