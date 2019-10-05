package nesemulator.cpu;

import nesemulator.cpu.opcodes.Opcode;

public class Instruction {
    public String name;
    public Opcode opcode;
    public Olc6502.AddressingMode addressingMode;
    public int cycles;

    public Instruction(String name, Opcode opcode, Olc6502.AddressingMode addressingMode, int cycles) {
        this.name = name;
        this.opcode = opcode;
        this.addressingMode = addressingMode;
        this.cycles = cycles;
    }
}
