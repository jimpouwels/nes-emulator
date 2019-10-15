package nl.pouwels.nes.ppu;

public interface Screen {

    void drawPixel(int cycle, int scanline, Pixel pixel);
}
