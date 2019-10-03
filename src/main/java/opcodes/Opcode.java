package opcodes;

public abstract class Opcode {

    abstract byte operate();

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
