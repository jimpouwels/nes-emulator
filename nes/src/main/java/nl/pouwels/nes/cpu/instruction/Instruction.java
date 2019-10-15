package nl.pouwels.nes.cpu.instruction;

/**
 * Opcode Base.
 */
public abstract class Instruction {

    public abstract int execute();

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
