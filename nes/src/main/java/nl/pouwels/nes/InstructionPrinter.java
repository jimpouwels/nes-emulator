package nl.pouwels.nes;

import nl.pouwels.nes.cpu.EventPrinter;

public class InstructionPrinter extends EventPrinter {

    public InstructionPrinter(boolean isEnabled) {
        super(isEnabled);
    }

    @Override
    protected void printInstructionLine(String line) {
        System.out.println(line);
    }
}
