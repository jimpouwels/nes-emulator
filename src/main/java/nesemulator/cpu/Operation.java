package nesemulator.cpu;

import nesemulator.cpu.instruction.Instruction;

public class Operation {
    public String name;
    public Instruction opcode;
    public Olc6502.AddressingMode addressingMode;
    public int cycles;

    public Operation(String name, Instruction instruction, Olc6502.AddressingMode addressingMode, int cycles) {
        this.name = name;
        this.opcode = instruction;
        this.addressingMode = addressingMode;
        this.cycles = cycles;
    }
}
