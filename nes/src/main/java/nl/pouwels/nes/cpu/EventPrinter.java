package nl.pouwels.nes.cpu;

import static nl.pouwels.nes.utils.PrintUtilities.printAsHex;

public abstract class EventPrinter implements EventHandler {

    private boolean isEnabled;

    public EventPrinter(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public void onNewInstruction(Operation operation, int opcode, int programCounter, int[] operands, int accumulatorRegister, int xRegister, int yRegister, int status, int stackPointer, int clockCount) {
        if (!isEnabled) {
            return;
        }
        String instructionLine = printAsHex(programCounter, 4) +
                "  " + printAsHex(opcode, 2) +
                printInstructionOperandBytes(operands) +
                String.format("%1$5s", operation.name) +
                " A:" + printAsHex(accumulatorRegister, 2) +
                " X:" + printAsHex(xRegister, 2) +
                " Y:" + printAsHex(yRegister, 2) +
                " P:" + printAsHex(status, 2) +
                " SP:" + printAsHex(stackPointer, 2) +
                " CYC:" + clockCount +
                " (AddressingMode: " + operation.addressingMode.getClass().getSimpleName() + ")";
        printInstructionLine(instructionLine);
    }

    protected abstract void printInstructionLine(String line);

    private String printInstructionOperandBytes(int[] bytes) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            result.append(i <= bytes.length - 1 ? " " + printAsHex(bytes[i], 2) : "   ");
        }
        return result.toString();
    }

}
