package nesemulator;

import nesemulator.cpu.EventPrinter;

public class InstructionPrinter extends EventPrinter {

    @Override
    protected void printInstructionLine(String line) {
        System.out.println(line);
    }
}
