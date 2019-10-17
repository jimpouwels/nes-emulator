package nl.pouwels.nes.ui;

import nl.pouwels.nes.Bus;
import nl.pouwels.nes.cpu.Olc6502;
import nl.pouwels.nes.ppu.Screen;
import nl.pouwels.nes.ppu.Sprite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;

public class MainScreen extends JPanel implements Screen, KeyListener {

    private static final String BACKGROUND_COLOR = "0x022f8e";
    private final BufferedImage gameCanvas;
    private Bus nes;
    private List<Olc6502.InstructionAtAddress> instructionLookup;
    private JTextPane textPane = new JTextPane();
    private boolean runningFullSpeed;

    public MainScreen() {
        renderWindow();
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();
        gameCanvas = new BufferedImage(256, 240, BufferedImage.TYPE_INT_RGB);
        drawInfoContainer();
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform imageSpaceTran = new AffineTransform();
        imageSpaceTran.scale(3f, 3f);
        g2.drawImage(gameCanvas, imageSpaceTran, null);
    }

    public void setBus(Bus nes) {
        this.nes = nes;
        instructionLookup = nes.getCpu().disassemble(0x0000, 0xFFFF);
        nes.reset();
        drawData();
    }

    @Override
    public void drawPixel(int x, int y, nl.pouwels.nes.ppu.Color color) {
        drawPixel(gameCanvas, x, y, color);
        if (x == 340 && y == 240) {
            drawData();
            repaint();
        }
    }

    @Override
    public void drawPatternTable(int tableIndex, Sprite sprite) {
        repaint();
    }

    public void drawPixel(BufferedImage canvas, int x, int y, nl.pouwels.nes.ppu.Color color) {
        int rgb = ((color.r & 0x0ff) << 16) | ((color.g & 0x0ff) << 8) | (color.b & 0x0ff);
        if (x < 256 && y < 240 && y > -1) {
            canvas.setRGB(x, y, rgb);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'i':
                runInstruction();
                drawData();
                break;
            case 'f':
                runFrame();
                break;
            case ' ':
                runProgram();
        }
    }

    private void runInstruction() {
        runningFullSpeed = false;
        do {
            nes.clock();
        } while (!nes.getCpu().isInstructionCompleted());
    }

    private void runFrame() {
        runningFullSpeed = false;
        do {
            nes.clock();
        } while (!nes.getPpu().isFrameCompleted());
        do {
            nes.clock();
        } while (!nes.getCpu().isInstructionCompleted());
    }

    private void runProgram() {
        runningFullSpeed = !runningFullSpeed;
        Runnable runnable = () -> {
            do {
                nes.clock();
            } while (runningFullSpeed);
        };
        new Thread(runnable).start();
    }

    private void drawSprite(BufferedImage canvas, Sprite sprite) {
        for (int y = 0; y < sprite.numRows(); y++) {
            for (int x = 0; x < sprite.numCols(); x++) {
                drawPixel(canvas, x, y, sprite.getPixel(x, y));
            }
        }
    }

    private void renderWindow() {
        JFrame frame = new JFrame("NES Emulator");
        frame.setPreferredSize(new Dimension(1250, 675));
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Color.decode(BACKGROUND_COLOR));
        setLayout(null);
    }

    private void drawInfoContainer() {
        textPane.setBounds(780, 10, 370, 630);
        Font f = new Font(Font.MONOSPACED, 0, 15);
        textPane.setFont(f);
        textPane.setForeground(Color.WHITE);
        textPane.setFocusable(false);
        textPane.setVisible(true);
        textPane.setBackground(Color.decode(BACKGROUND_COLOR));
        add(textPane);
    }

    private boolean isKeyPressed(int keyChar, int i2) {
        return keyChar == i2;
    }

    private void drawData() {
        DataRenderer.drawData(instructionLookup, textPane, nes);
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}