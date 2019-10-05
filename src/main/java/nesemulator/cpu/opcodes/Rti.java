package nesemulator.cpu.opcodes;

/**
 * Return from Interrupt.
 */
public class Rti extends Opcode {
    @Override
    public byte operate() {
        return 0;
    }
}
