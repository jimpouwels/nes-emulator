package nl.pouwels.nes.ppu.register;

public class MaskRegister extends Register {
    public int grayScale_1;
    public int renderBackgroundLeft_1;
    public int renderSpritesLeft_1;
    public int renderBackground_1;
    public int renderSprites_1;
    public int enhanceRed_1;
    public int enhanceGreen_1;
    public int enhanceBlue_1;

    @Override
    public void write(int data_8) {
        grayScale_1 = getBitValue(data_8, 0);
        renderBackgroundLeft_1 = getBitValue(data_8, 1);
        renderSpritesLeft_1 = getBitValue(data_8, 2);
        renderBackground_1 = getBitValue(data_8, 3);
        renderSprites_1 = getBitValue(data_8, 4);
        enhanceRed_1 = getBitValue(data_8, 5);
        enhanceGreen_1 = getBitValue(data_8, 6);
        enhanceBlue_1 = getBitValue(data_8, 7);
    }

    @Override
    public int getAsByte() {
        return 0;
    }

    @Override
    public void incrementWith(int incrementValue) {

    }

}
