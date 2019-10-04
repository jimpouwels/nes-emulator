package opcodes;

public class InvalidOpcode extends Opcode {
    @Override
    public byte operate() {
        return 0;
    }

    @Override
    public String toString() {
        return "???";
    }
}
