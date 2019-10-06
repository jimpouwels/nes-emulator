package nesemulator.cpu.instruction;

/**
 * Return from Interrupt.
 */
public class Rti extends Instruction {
    @Override
    public byte operate() {
        return 0;
    }
}
