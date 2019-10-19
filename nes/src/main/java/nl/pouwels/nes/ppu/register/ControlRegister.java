package nl.pouwels.nes.ppu.register;

public class ControlRegister extends Register {
    public int nametableX_1;
    public int nametableY_1;
    public int incrementMode_1;
    public int patternSprite_1;
    public int patternBackground_1;
    public int spriteSize_1;
    public int slaveMode_1; // unused
    public int enable_Nmi_1;

    @Override
    public void write(int data_8) {
        nametableX_1 = getBitValue(data_8, 0);
        nametableY_1 = getBitValue(data_8, 1);
        incrementMode_1 = getBitValue(data_8, 2);
        patternSprite_1 = getBitValue(data_8, 3);
        patternBackground_1 = getBitValue(data_8, 4);
        spriteSize_1 = getBitValue(data_8, 5);
        slaveMode_1 = getBitValue(data_8, 6);
        enable_Nmi_1 = getBitValue(data_8, 7);
    }

    @Override
    public int getAsByte() {
        return 0;
    }

    @Override
    public void incrementWith(int incrementValue) {

    }
}
