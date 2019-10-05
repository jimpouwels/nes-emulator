package nesemulator.cpu.opcodes;

/**
 * Invalid Opcode.
 */
public class InvalidOpcode extends Opcode {
    @Override
    public short operate() {
        return 0;
    }

    @Override
    public String toString() {
        return "???";
    }
}
