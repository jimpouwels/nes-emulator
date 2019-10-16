package nl.pouwels.nes.ppu;

public class Sprite {

    private Pixel[][] pixels;

    public Sprite(int width, int height) {
        pixels = new Pixel[width][height];
    }

    public void setPixel(int x, int y, Pixel pixel) {
        pixels[x][y] = pixel;
    }

    public Pixel getPixel(int x, int y) {
        return pixels[x][y];
    }
}
