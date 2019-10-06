package nesemulator.cpu.instruction;

/**
 * Return from Subroutine.
 */
public class Rts extends Instruction {
    @Override
    public byte execute() {
        return 0;
    }
}
