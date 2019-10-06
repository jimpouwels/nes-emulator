package nesemulator.cpu.instruction;

/**
 * Clear Interrupt Disable Bit.
 */
public class Cli extends Instruction {
    @Override
    public byte operate() {
        return 0;
    }
}
