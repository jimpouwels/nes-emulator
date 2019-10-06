package nesemulator.cpu.instruction;

/**
 * Clear Interrupt Disable Bit.
 */
public class Cli extends Instruction {
    @Override
    public byte execute() {
        return 0;
    }
}
