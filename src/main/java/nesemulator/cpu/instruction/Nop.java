package nesemulator.cpu.instruction;

/**
 * No Operation.
 */
public class Nop extends Instruction {
    @Override
    public byte operate() {
        return 0;
    }
}
