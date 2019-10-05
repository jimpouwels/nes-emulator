package nesemulator.cpu.addressmode;

import nesemulator.cpu.Olc6502;

/**
 * Relative Addressing.
 */
public class Rel extends AddressingMode {

    public Rel(Olc6502 cpu) {
        super(cpu);
    }

    @Override
    public short set() {
        return 0;
    }
}