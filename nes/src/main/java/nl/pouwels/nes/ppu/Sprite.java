package nl.pouwels.nes.ppu;

public class Sprite {

    private Color[][] colors;

    public Sprite(int width, int height) {
        colors = new Color[width][height];
    }

    public void setPixel(int x, int y, Color color) {
        colors[x][y] = color;
    }

    public Color getPixel(int x, int y) {
        return colors[x][y];
    }
}
