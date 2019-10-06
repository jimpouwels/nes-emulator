package nesemulator.cpu.instruction;

/**
 * No Operation.
 */
public class Nop extends Instruction {
    @Override
    public byte execute() {
        return 0;
    }
}
