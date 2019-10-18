package nl.pouwels.nes.ui;

import nl.pouwels.nes.Bus;
import nl.pouwels.nes.cpu.Olc6502;
import nl.pouwels.nes.ppu.Screen;
import nl.pouwels.nes.ppu.Sprite;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;

public class MainScreen extends JPanel implements Screen, KeyListener {

    private static final String BACKGROUND_COLOR = "0x022f8e";
    private final BufferedImage gameCanvas;
    private final BufferedImage patternTable1Canvas;
    private final BufferedImage patternTable2Canvas;
    private Bus nes;
    private List<Olc6502.InstructionAtAddress> instructionLookup;
    private JTextPane textPane = new JTextPane();
    private boolean runningFullSpeed;
    private int palletteIndex = 0;


    public MainScreen() {
        renderWindow();
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();
        gameCanvas = new BufferedImage(256, 240, BufferedImage.TYPE_INT_RGB);
        patternTable1Canvas = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
        patternTable2Canvas = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
        drawInfoContainer();
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform imageSpaceTran = new AffineTransform();
        imageSpaceTran.scale(3.5f, 3.5f);
        g2.drawImage(gameCanvas, imageSpaceTran, null);

        AffineTransform patternTableTransform = new AffineTransform();
        patternTableTransform.scale(2.2f, 2.2);
        g2.drawImage(patternTable1Canvas, 910, 550, null);
        g2.drawImage(patternTable2Canvas, 1100, 550, null);
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
        for (int y = 0; y < sprite.numCols(); y++) {
            for (int x = 0; x < sprite.numRows(); x++) {
                nl.pouwels.nes.ppu.Color pixel = sprite.getPixel(x, y);
                int rgb = ((pixel.r & 0x0ff) << 16) | ((pixel.g & 0x0ff) << 8) | (pixel.b & 0x0ff);
                if (tableIndex == 0) {
                    patternTable1Canvas.setRGB(x, y, rgb);
                } else if (tableIndex == 1) {
                    patternTable2Canvas.setRGB(x, y, rgb);
                }
            }
        }
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
                break;
            case 'p':
                rotatePallette();
                break;
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
        Runnable patternTableLoader = () -> {
            try {
                Thread.sleep(1000);
                loadPatternTables();
            } catch (InterruptedException e) {
            }
        };
        new Thread(patternTableLoader).start();
    }

    private void rotatePallette() {
        palletteIndex++;
        if (palletteIndex > 7) {
            palletteIndex = 0;
        }
        loadPatternTables();
    }

    private void loadPatternTables() {
        nes.getPpu().getPatternTable(0, palletteIndex);
        nes.getPpu().getPatternTable(1, palletteIndex);
    }

    private void renderWindow() {
        JFrame frame = new JFrame("NES Emulator");
        frame.setPreferredSize(new Dimension(1350, 870));
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Color.decode(BACKGROUND_COLOR));
        setLayout(null);
    }

    private void drawInfoContainer() {
        textPane.setBounds(910, 10, 370, 520);
        Font f = new Font(Font.MONOSPACED, 0, 15);
        textPane.setFont(f);
        textPane.setForeground(Color.WHITE);
        textPane.setFocusable(false);
        textPane.setVisible(true);
        textPane.setParagraphAttributes(new SimpleAttributeSet(), false);
        textPane.setBackground(Color.decode(BACKGROUND_COLOR));
        add(textPane);
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