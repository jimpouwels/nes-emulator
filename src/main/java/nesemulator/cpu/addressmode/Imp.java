package nesemulator.cpu.addressmode;

import nesemulator.cpu.Olc6502;

/**
 * Implied Addressing.
 * <p>
 * Operand is implicitly stated in the instruction's opcode.
 * However, it could be operating on the accumulator, setting the fetched value to the accumulator register value.
 */
public class Imp extends AddressingMode {

    public Imp(Olc6502 cpu) {
        super(cpu);
    }

    @Override
    public byte set() {
        cpu.fetched = cpu.accumulatorRegister;
        return 0;
    }
}