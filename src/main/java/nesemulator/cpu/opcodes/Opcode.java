package nesemulator.cpu.opcodes;

/**
 * Opcode Base.
 */
public abstract class Opcode {

    public abstract short operate();

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
