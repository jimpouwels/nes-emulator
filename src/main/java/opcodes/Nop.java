package opcodes;

public class Nop extends Opcode {
    @Override
    byte operate() {
        return 0;
    }
}
