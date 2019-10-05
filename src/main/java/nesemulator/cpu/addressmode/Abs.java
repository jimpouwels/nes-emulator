package nesemulator.cpu.addressmode;

import nesemulator.cpu.Olc6502;

/**
 * Absolute Addressing.
 */
public class Abs extends AddressingMode {

    public Abs(Olc6502 cpu) {
        super(cpu);
    }

    @Override
    public short set() {
        return 0;
    }
}