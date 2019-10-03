package opcodes;

public class InvalidOpcode extends Opcode {
    @Override
    byte operate() {
        return 0;
    }

    @Override
    public String toString() {
        return "???";
    }
}
