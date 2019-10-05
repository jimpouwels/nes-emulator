package nesemulator.cpu.addressmode;

import nesemulator.cpu.Olc6502;

/**
 * Absolute Indirect.
 */
public class Ind extends AddressingMode {

    public Ind(Olc6502 cpu) {
        super(cpu);
    }

    @Override
    public byte set() {
        return 0;
    }
}