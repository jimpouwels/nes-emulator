package nl.pouwels.nes.ui;

import nl.pouwels.nes.ppu.Color;
import nl.pouwels.nes.ppu.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class MainScreen extends JPanel implements Screen {

    private final BufferedImage canvas;

    public MainScreen() {
        renderWindow();
        renderStatus();
        canvas = new BufferedImage(341, 261, BufferedImage.TYPE_INT_RGB);
    }

    @Override
    public void drawPixel(int x, int y, Color color) {
        int rgb = ((color.r & 0x0ff) << 16) | ((color.g & 0x0ff) << 8) | (color.b & 0x0ff);
        canvas.setRGB(x, y, rgb);

        if (x == 0 && y == 0) {
            repaint();
        }
    }

    private void renderStatus() {
        Label statusTitle = new Label();
        statusTitle.setText("STATUS");
        statusTitle.setForeground(java.awt.Color.WHITE);
        statusTitle.setBounds(875, 0, 100, 40);
        add(statusTitle);
    }

    private void renderWindow() {
        JFrame frame = new JFrame("NES Emulator");
        frame.setPreferredSize(new Dimension(1250, 675));
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(java.awt.Color.decode("0x022f8e"));
        setLayout(null);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform imageSpaceTran = new AffineTransform();
        imageSpaceTran.scale(2.5f, 2.5f);
        g2.drawImage(canvas, imageSpaceTran, null);
    }
}