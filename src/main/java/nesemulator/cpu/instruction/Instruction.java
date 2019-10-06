package nesemulator.cpu.instruction;

/**
 * Opcode Base.
 */
public abstract class Instruction {

    public abstract byte operate();

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
