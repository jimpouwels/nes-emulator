package nesemulator.cpu.opcode;

/**
 * No Operation.
 */
public class Nop extends Instruction {
    @Override
    public byte operate() {
        return 0;
    }
}
