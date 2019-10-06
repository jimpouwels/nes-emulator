package nesemulator.cpu.opcode;

/**
 * Force Break.
 */
public class Brk extends Instruction {
    @Override
    public byte operate() {
        return 0;
    }
}
