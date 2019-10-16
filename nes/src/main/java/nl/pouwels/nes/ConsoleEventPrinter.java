package nl.pouwels.nes;

import nl.pouwels.nes.cpu.EventPrinter;

public class ConsoleEventPrinter extends EventPrinter {

    public ConsoleEventPrinter(boolean isEnabled) {
        super(isEnabled);
    }

    @Override
    protected void printInstructionLine(String line) {
        System.out.println(line);
    }
}
