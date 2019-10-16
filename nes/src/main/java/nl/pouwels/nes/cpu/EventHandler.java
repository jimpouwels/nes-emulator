package nl.pouwels.nes.cpu;

public interface EventHandler {
    void onNewInstruction(Operation operation, int opcode, int programCounter, int[] operands, int accumulatorRegister, int xRegister, int yRegister, int status, int stackPointer, int clockCount);
}
