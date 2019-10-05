package nesemulator.cpu.addressmode;

import nesemulator.cpu.Olc6502;

/**
 * Indexed (Y) Indirect Addressing.
 */
public class Izy extends AddressingMode {

    public Izy(Olc6502 cpu) {
        super(cpu);
    }

    @Override
    public byte set() {
        return 0;
    }
}