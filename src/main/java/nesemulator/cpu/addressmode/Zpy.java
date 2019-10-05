package nesemulator.cpu.addressmode;

import nesemulator.cpu.Olc6502;

/**
 * Indexed (Y) Zero Page Addressing.
 */
public class Zpy extends AddressingMode {

    public Zpy(Olc6502 cpu) {
        super(cpu);
    }

    @Override
    public byte set() {
        return 0;
    }
}