package nesemulator.cpu.instruction;

/**
 * No Operation.
 */
public class Nop extends Instruction {
    @Override
    public int execute() {
        return 0;
    }
}
