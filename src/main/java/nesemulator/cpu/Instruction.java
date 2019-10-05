package nesemulator.cpu;

import nesemulator.cpu.addressmode.AddressingMode;
import nesemulator.cpu.opcodes.Opcode;

public class Instruction {
    public String name;
    public Opcode opcode;
    public nesemulator.cpu.addressmode.AddressingMode addressingMode;
    public int cycles;

    public Instruction(String name, Opcode opcode, AddressingMode addressingMode, int cycles) {
        this.name = name;
        this.opcode = opcode;
        this.addressingMode = addressingMode;
        this.cycles = cycles;
    }
}
