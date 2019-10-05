package nesemulator.cpu.addressmode;

import nesemulator.cpu.Olc6502;

import static nesemulator.utils.ByteUtilities.sumUnsignedAndWiden;

/**
 * Indexed (X) Zero Page Addressing.
 */
public class Zpx extends AddressingMode {

    public Zpx(Olc6502 cpu) {
        super(cpu);
    }

    @Override
    public byte set() {
        cpu.addrAbs = sumUnsignedAndWiden(cpu.read(cpu.programCounter), cpu.xRegister);
        cpu.programCounter += 1;
        cpu.addrAbs &= 0x00FF;
        return 0;
    }
}