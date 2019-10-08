package nesemulator.cpu.instruction;

/**
 * Invalid Opcode.
 */
public class InvalidInstruction extends Instruction {
    @Override
    public int execute() {
        return 0;
    }

    @Override
    public String toString() {
        return "???";
    }
}
