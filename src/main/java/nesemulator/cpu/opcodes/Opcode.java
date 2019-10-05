package nesemulator.cpu.opcodes;

/**
 * Opcode Base.
 */
public abstract class Opcode {

    public abstract byte operate();

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
