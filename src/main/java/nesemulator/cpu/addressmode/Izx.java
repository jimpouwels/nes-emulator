package nesemulator.cpu.addressmode;

import nesemulator.cpu.Olc6502;

/**
 * Indexed (X) Indirect Addressing.
 */
public class Izx extends AddressingMode {

    public Izx(Olc6502 cpu) {
        super(cpu);
    }

    @Override
    public short set() {
        return 0;
    }
}