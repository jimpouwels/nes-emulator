package nesemulator.cpu.addressmode;

import nesemulator.cpu.Olc6502;

public abstract class AddressingMode {

    protected final Olc6502 cpu;

    public abstract byte set();

    public AddressingMode(Olc6502 cpu) {
        this.cpu = cpu;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
