package nl.pouwels.nes.ppu;

public class Sprite {

    private Color[][] colors;
    private int width;
    private int height;

    public Sprite(int width, int height) {
        this.width = width;
        this.height = height;
        colors = new Color[width][height];
    }

    public void setPixel(int x, int y, Color color) {
        colors[x][y] = color;
    }

    public Color getPixel(int x, int y) {
        return colors[x][y];
    }

    public int numRows() {
        return height;
    }

    public int numCols() {
        return width;
    }
}
