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
        return (verticalBlank_1 << 7) | (spriteZeroHit_1 << 6) | (spriteOverflow_1 << 5) | unused_5;
    }

    @Override
    public void incrementWith(int incrementValue) {

    }
}
