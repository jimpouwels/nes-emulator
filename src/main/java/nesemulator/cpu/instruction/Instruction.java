package nesemulator.cpu.instruction;

/**
 * Opcode Base.
 */
public abstract class Instruction {

    public abstract byte execute();

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
