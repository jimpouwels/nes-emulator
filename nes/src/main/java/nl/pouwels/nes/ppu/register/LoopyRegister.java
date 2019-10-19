package nl.pouwels.nes.ppu.register;

public class LoopyRegister extends Register {
    public int coarseX_5;
    public int coarseY_5;
    public int nametableX_1;
    public int nametableY_1;
    public int fineY_3;
    public int unused_1;

    @Override
    public void write(int data_8) {
        coarseX_5 = data_8 & 0x31;
        coarseY_5 = (data_8 >> 5) & 0x31;
        nametableX_1 = getBitValue(data_8, 10);
        nametableY_1 = getBitValue(data_8, 11);
        nametableY_1 = getBitValue(data_8, 12);
        unused_1 = getBitValue(data_8, 13);
    }

    @Override
    public int getAsByte() {
        return coarseX_5 | (coarseY_5 << 5) | (nametableX_1 << 10) | (nametableY_1 << 11) | (fineY_3 << 12) | (unused_1 << 15);
    }

    @Override
    public void incrementWith(int incrementValue) {
        write(getAsByte() + incrementValue);
    }
}
