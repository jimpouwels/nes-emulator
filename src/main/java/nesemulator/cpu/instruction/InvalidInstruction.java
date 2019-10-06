package nesemulator.cpu.instruction;

/**
 * Invalid Opcode.
 */
public class InvalidInstruction extends Instruction {
    @Override
    public byte execute() {
        return 0;
    }

    @Override
    public String toString() {
        return "???";
    }
}
