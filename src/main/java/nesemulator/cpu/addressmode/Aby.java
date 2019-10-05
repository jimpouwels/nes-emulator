package nesemulator.cpu.addressmode;

import nesemulator.cpu.Olc6502;

/**
 * Indexed (Y) Absolute Addressing.
 */
public class Aby extends AddressingMode {

    public Aby(Olc6502 cpu) {
        super(cpu);
    }

    @Override
    public short set() {
        return 0;
    }
}