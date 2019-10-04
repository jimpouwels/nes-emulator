package opcodes;

public class Nop extends Opcode {
    @Override
    public byte operate() {
        return 0;
    }
}
