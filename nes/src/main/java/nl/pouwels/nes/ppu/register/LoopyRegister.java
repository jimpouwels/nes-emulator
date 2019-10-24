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
        coarseY_5 = (data >> 5) & 0x1F;
        nametableX_1 = (data >> 10) & 0x01;
        nametableY_1 = (data >> 11) & 0x01;
        fineY_3 = (data >> 12) & 0x07;
        unused_1 = (data >> 15) & 0x01;
    }

    @Override
    public int get() {
        return (unused_1 << 15) | (fineY_3 << 12) | (nametableY_1 << 11) | (nametableX_1 << 10) | (coarseY_5 << 5) | coarseX_5;
    }

    @Override
    public void incrementWith(int incrementValue) {
        write(get() + incrementValue);
    }
}
