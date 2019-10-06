package nesemulator.cpu.instruction;

/**
 * Invalid Opcode.
 */
public class InvalidInstruction extends Instruction {
    @Override
    public byte operate() {
        return 0;
    }

    @Override
    public String toString() {
        return "???";
    }
}
