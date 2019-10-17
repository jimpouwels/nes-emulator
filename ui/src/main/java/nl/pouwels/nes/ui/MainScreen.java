package nl.pouwels.nes.ui;

import nl.pouwels.nes.Bus;
import nl.pouwels.nes.ppu.Color;
import nl.pouwels.nes.ppu.Screen;
import nl.pouwels.nes.ppu.Sprite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class MainScreen extends JPanel implements Screen, KeyListener {

    private static final int SPACEBAR = 32;
    private final BufferedImage gameCanvas;
    private BufferedImage patternTable1;
    private BufferedImage patternTable2;
    private Bus nes;

    public MainScreen() {
        renderWindow();
        renderStatus();
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();
        gameCanvas = new BufferedImage(341, 261, BufferedImage.TYPE_INT_RGB);
    }

    public void setBus(Bus nes) {
        this.nes = nes;
    }

    @Override
    public void drawPixel(int x, int y, Color color) {
        drawPixel(gameCanvas, x, y, color);
        if (x == 340 && y == 240) {
            repaint();
        }
    }

    @Override
    public void drawPatternTable(int tableIndex, Sprite sprite) {
        if (tableIndex == 0) {
            drawPatternTable1(sprite);
        } else if (tableIndex == 1) {
            drawPatternTable2(sprite);
        } else {
            throw new RuntimeException("Unknown table index");
        }
        repaint();
    }


    private void drawPatternTable1(Sprite patternTable) {
        patternTable1 = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
        drawSprite(patternTable1, patternTable);
    }

    private void drawPatternTable2(Sprite patternTable) {
        patternTable2 = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
        drawSprite(patternTable2, patternTable);
    }

    public void drawPixel(BufferedImage canvas, int x, int y, Color color) {
        int rgb = ((color.r & 0x0ff) << 16) | ((color.g & 0x0ff) << 8) | (color.b & 0x0ff);
        if (x < 256 && y < 240 && y > -1) {
            canvas.setRGB(x, y, rgb);
        }
    }

    private void drawSprite(BufferedImage canvas, Sprite sprite) {
        for (int y = 0; y < sprite.numRows(); y++) {
            for (int x = 0; x < sprite.numCols(); x++) {
                drawPixel(canvas, x, y, sprite.getPixel(x, y));
            }
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
        g2.drawImage(gameCanvas, imageSpaceTran, null);
        g2.drawImage(patternTable1, 500, 500, null);
        g2.drawImage(patternTable2, 500, 500, null);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getExtendedKeyCode() == SPACEBAR) {
            while (!nes.getCpu().isInstructionCompleted()) {
                nes.clock();
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}