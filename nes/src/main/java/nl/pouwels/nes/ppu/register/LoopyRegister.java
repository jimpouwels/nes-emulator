package nl.pouwels.nes.ppu.register;

public class LoopyRegister extends Register {
    public int coarseX_5;
    public int coarseY_5;
    public int nametableX_1;
    public int nametableY_1;
    public int fineY_3;
    public int unused_1;

    @Override
    public void write(int data) {
        coarseX_5 = data & 0x001F;
        coarseY_5 = (data >> 5) & 0x001F;
        nametableX_1 = (data >> 10) & 0x01;
        nametableY_1 = (data >> 11) & 0x01;
        fineY_3 = (data >> 12) & 0x0007;
        unused_1 = (data >> 15) & 0x01;
    }

    @Override
    public int get() {
        return coarseX_5 | (coarseY_5 << 5) | (nametableX_1 << 10) | (nametableY_1 << 11) | (fineY_3 << 12) | (unused_1 << 15);
    }

    @Override
    public void incrementWith(int incrementValue) {
        write(get() + incrementValue);
    }
}
