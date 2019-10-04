package opcodes;

public class InvalidOpcode extends Opcode {
    @Override
    public short operate() {
        return 0;
    }

    @Override
    public String toString() {
        return "???";
    }
}
