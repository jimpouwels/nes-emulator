package nesemulator.cpu.instruction;

/**
 * Force Break.
 */
public class Brk extends Instruction {
    @Override
    public byte execute() {
        return 0;
    }
}
