package nl.pouwels.nes.ppu;

public interface Screen {

    void drawPixel(int cycle, int scanline, Color color);

    void drawPatternTable(int tableIndex, Sprite sprite);
}
