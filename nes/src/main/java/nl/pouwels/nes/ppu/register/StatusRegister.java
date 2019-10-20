package nl.pouwels.nes.ppu.register;

public class StatusRegister extends Register {
    public int unused_5;
    public int spriteOverflow_1;
    public int spriteZeroHit_1;
    public int verticalBlank_1;

    @Override
    public void write(int data_8) {

    }

    @Override
    public int get() {
        return unused_5 | (spriteOverflow_1 << 5) | (spriteZeroHit_1 << 6) | (verticalBlank_1 << 7);
    }

    @Override
    public void incrementWith(int incrementValue) {

    }
}
