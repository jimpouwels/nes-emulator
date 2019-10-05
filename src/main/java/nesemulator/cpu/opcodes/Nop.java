package nesemulator.cpu.opcodes;

/**
 * No Operation.
 */
public class Nop extends Opcode {
    @Override
    public byte operate() {
        return 0;
    }
}
