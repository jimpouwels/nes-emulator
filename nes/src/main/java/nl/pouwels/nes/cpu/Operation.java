package nl.pouwels.nes.cpu;

import nl.pouwels.nes.cpu.instruction.Instruction;

public class Operation {
    public String name;
    public Instruction instruction;
    public Olc6502.AddressingMode addressingMode;
    public int cycles;
    public int nrOfBytes;

    public Operation(String name, Instruction instruction, Olc6502.AddressingMode addressingMode, int cycles, int nrOfBytes) {
        this.name = name;
        this.instruction = instruction;
        this.addressingMode = addressingMode;
        this.cycles = cycles;
        this.nrOfBytes = nrOfBytes;
    }
}
