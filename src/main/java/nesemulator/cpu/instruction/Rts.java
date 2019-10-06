package nesemulator.cpu.instruction;

/**
 * Return from Subroutine.
 */
public class Rts extends Instruction {
    @Override
    public byte operate() {
        return 0;
    }
}
