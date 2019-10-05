package nesemulator.cpu.addressmode;

import nesemulator.cpu.Olc6502;

/**
 * Zero Page Addressing.
 */
public class Zp0 extends AddressingMode {

    public Zp0(Olc6502 cpu) {
        super(cpu);
    }

    @Override
    public short set() {
        return 0;
    }
}