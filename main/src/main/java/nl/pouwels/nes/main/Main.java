package nl.pouwels.nes.main;

import nl.pouwels.nes.Bus;
import nl.pouwels.nes.InstructionPrinter;
import nl.pouwels.nes.cartridge.Cartridge;
import nl.pouwels.nes.cpu.Olc6502;
import nl.pouwels.nes.ppu.Olc2c02;
import nl.pouwels.nes.ui.MainScreen;

public class Main {

    public static void main(String... args) {
        MainScreen screen = new MainScreen();
        screen.setVisible(true);


        Runnable r = () -> {
            Cartridge cartridge = new Cartridge(Main.class.getClassLoader().getResource("nestest.nes").getPath());
            Olc6502 cpu = new Olc6502(new InstructionPrinter());
            Bus nes = new Bus(cpu, new Olc2c02(screen));
            cpu.connectToBus(nes);
            nes.insertCartridge(cartridge);
            System.out.println("Starting testrom...");
            nes.reset(0xC000);
            nes.start();
        };
        new Thread(r).start();

    }

}
