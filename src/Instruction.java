import addressmode.AddressingMode;
import opcodes.Opcode;

public class Instruction {
    public String name;
    public Opcode opcode;
    public AddressingMode addressingMode;
    public int cycles;

    public Instruction(String name, Opcode opcode, AddressingMode addressingMode, int cycles) {
        this.name = name;
        this.opcode = opcode;
        this.addressingMode = addressingMode;
        this.cycles = cycles;
    }
}
