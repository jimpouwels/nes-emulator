package nesemulator.cpu.addressmode;

import nesemulator.cpu.Olc6502;

/**
 * Immediate Address.
 * The data of the instruction is in the second byte of the instruction.
 * Set the addressAbsolute of the cpu to the incremented programCounter value.
 */
public class Imm extends AddressingMode {

    public Imm(Olc6502 cpu) {
        super(cpu);
    }

    @Override
    public short set() {
        cpu.setAddressAbsolute(cpu.incrementProgramCounter());
        return 0;
    }
}