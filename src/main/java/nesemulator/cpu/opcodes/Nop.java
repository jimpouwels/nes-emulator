package nesemulator.cpu.opcodes;

/**
 * No Operation.
 */
public class Nop extends Opcode {
    @Override
    public short operate() {
        return 0;
    }
}
