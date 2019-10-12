package nesemulator.cpu;

public abstract class EventPrinter {

    public void onNewInstruction(Operation operation, int opcode, int programCounter, int[] operands, int accumulatorRegister, int xRegister, int yRegister, int status, int stackPointer, int clockCount) {
        String instructionLine = printAsHex(programCounter, 4) +
                "  " + printAsHex(opcode, 2) +
                printInstructionOperandBytes(operands) +
//                            " " + printAsHex(addrAbs_16) +
                "  " + operation.name +
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
        String result = "";
        for (int i = 0; i < 2; i++) {
            result += i <= bytes.length - 1 ? " " + printAsHex(bytes[i], 2) : "   ";
        }
        return result;
    }

    private String printAsHex(int value, int charCount) {
        return String.format("%0" + charCount + "x", value).toUpperCase();
    }
}
