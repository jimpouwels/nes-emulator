package opcodes;

public abstract class Opcode {

    public abstract short operate();

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
