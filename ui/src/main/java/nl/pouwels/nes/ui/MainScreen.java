package nl.pouwels.nes.ui;

import nl.pouwels.nes.ppu.Pixel;
import nl.pouwels.nes.ppu.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MainScreen extends JPanel implements Screen {

    private final BufferedImage canvas;
    private BufferedImage img = new BufferedImage(500, 300, BufferedImage.TYPE_INT_RGB);

    public MainScreen() {
        JFrame frame = new JFrame("NES Emulator");
        frame.setPreferredSize(new Dimension(400, 300));
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(canvas, null, null);
    }

    @Override
    public void drawPixel(int cycle, int scanline, Pixel pixel) {
        int rgb = ((pixel.r & 0x0ff) << 16) | ((pixel.g & 0x0ff) << 8) | (pixel.b & 0x0ff);
        canvas.setRGB(cycle, scanline, rgb);
        repaint();
    }
}