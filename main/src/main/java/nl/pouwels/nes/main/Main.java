package nl.pouwels.nes.main;

import nl.pouwels.nes.Bus;
import nl.pouwels.nes.cartridge.Cartridge;
import nl.pouwels.nes.cartridge.CartridgeLoader;
import nl.pouwels.nes.cpu.EventPrinter;
import nl.pouwels.nes.cpu.Olc6502;
import nl.pouwels.nes.ppu.Olc2c02;
import nl.pouwels.nes.ui.MainScreen;

import java.io.FileWriter;
import java.io.PrintWriter;

public class Main {

    public static void main(String... args) {
        MainScreen screen = new MainScreen();
        screen.setVisible(true);
        Cartridge cartridge = CartridgeLoader.loadCartridge(Main.class.getClassLoader().getResource("mapper4/smb2.nes").getPath());
        Olc6502 cpu = new Olc6502(new LogFileEventPrinter(false));
        Bus nes = new Bus(cpu, new Olc2c02(screen));
        cpu.connectToBus(nes);
        nes.insertCartridge(cartridge);
        screen.setBus(nes);
    }

    static class LogFileEventPrinter extends EventPrinter {

        public LogFileEventPrinter(boolean isEnabled) {
            super(isEnabled);
        }

        @Override
        protected void printInstructionLine(String line) {
            try (PrintWriter output = new PrintWriter(new FileWriter("test.log", true))) {
                output.printf("%s\r\n", line);
            } catch (Exception e) {
            }
        }
    }

}
