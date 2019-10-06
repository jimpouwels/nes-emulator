package nesemulator.cpu.instruction;

/**
 * Return from Interrupt.
 */
public class Rti extends Instruction {
    @Override
    public byte execute() {
        return 0;
    }
}
