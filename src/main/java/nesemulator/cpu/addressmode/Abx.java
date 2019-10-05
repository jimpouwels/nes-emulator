package nesemulator.cpu.addressmode;

import nesemulator.cpu.Olc6502;

/**
 * Indexed (X) Absolute Addressing.
 */
public class Abx extends AddressingMode {

    public Abx(Olc6502 cpu) {
        super(cpu);
    }

    @Override
    public short set() {
        return 0;
    }
}