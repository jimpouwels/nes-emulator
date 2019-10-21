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
    private final BufferedImage patternTableCanvas;
    private Bus nes;
    private List<Olc6502.InstructionAtAddress> instructionLookup;
    private JTextPane textPane = new JTextPane();
    private boolean runningFullSpeed;
    private int palletteIndex = 0;
    private LeftPanel leftPanel = new LeftPanel();
    private RightPanel rightPanel = new RightPanel();

    public MainScreen() {
        renderWindow();
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();
        gameCanvas = new BufferedImage(256, 240, BufferedImage.TYPE_INT_RGB);
        patternTableCanvas = new BufferedImage(256, 128, BufferedImage.TYPE_INT_RGB);
        setLayout(new BorderLayout());
        getRootPane().setLayout(null);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    class LeftPanel extends JPanel {

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            AffineTransform imageSpaceTran = new AffineTransform();
            imageSpaceTran.scale(3.5f, 3.5f);
            g2.drawImage(gameCanvas, imageSpaceTran, null);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(895, 500);
        }
    }

    class RightPanel extends JPanel {

        public DataPanel dataPanel = new DataPanel();
        public PatternTablePanel patternTablePanel = new PatternTablePanel();

        public RightPanel() {
            setLayout(new BorderLayout());
            add(dataPanel, BorderLayout.NORTH);
            add(patternTablePanel, BorderLayout.SOUTH);
            setBackground(Color.decode(BACKGROUND_COLOR));
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(588, 900);
        }
    }

    class DataPanel extends JPanel {
        public DataPanel() {
            setLayout(null);
            setBackground(Color.decode(BACKGROUND_COLOR));
            textPane.setBounds(20, 10, 500, 550);
            Font f = new Font(Font.MONOSPACED, 0, 15);
            textPane.setFont(f);
            textPane.setForeground(Color.WHITE);
            textPane.setFocusable(false);
            textPane.setVisible(true);
            textPane.setParagraphAttributes(new SimpleAttributeSet(), false);
            textPane.setBackground(Color.decode(BACKGROUND_COLOR));
            add(textPane);
        }

        public void drawData() {
            DataRenderer.drawData(instructionLookup, textPane, nes);
            repaint();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(290, 540);
        }
    }

    class PatternTablePanel extends JPanel {

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            AffineTransform patternTableTransform = new AffineTransform();
            patternTableTransform.scale(2.3f, 2.3f);
            g2.drawImage(patternTableCanvas, patternTableTransform, null);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(290, 294);
        }

    }

    public void setBus(Bus nes) {
        this.nes = nes;
        instructionLookup = nes.getCpu().disassemble(0x0000, 0xFFFF);
        nes.reset();
        rightPanel.dataPanel.drawData();
    }

    @Override
    public void drawPixel(int x, int y, nl.pouwels.nes.ppu.Color color) {
        drawPixel(gameCanvas, x, y, color);
        if (x == 339 && y == 260) {
            rightPanel.dataPanel.drawData();
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
                    patternTableCanvas.setRGB(x, y, rgb);
                } else if (tableIndex == 1) {
                    patternTableCanvas.setRGB(x + 128, y, rgb);
                }
            }
        }
        rightPanel.patternTablePanel.repaint();
    }

    public void drawPixel(BufferedImage canvas, int x, int y, nl.pouwels.nes.ppu.Color color) {
        int rgb = ((color.r & 0x0ff) << 16) | ((color.g & 0x0ff) << 8) | (color.b & 0x0ff);
        if (x < 256 && y < 240 && y > -1) {
            if (x >= 0) {
                canvas.setRGB(x, y, rgb);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'i':
                runInstruction();
                rightPanel.dataPanel.drawData();
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

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_UP:
                nes.controllers_8[0] |= 0x08;
                break;
            case KeyEvent.VK_DOWN:
                nes.controllers_8[0] |= 0x04;
                break;
            case KeyEvent.VK_LEFT:
                nes.controllers_8[0] |= 0x02;
                break;
            case KeyEvent.VK_RIGHT:
                nes.controllers_8[0] |= 0x01;
                break;
        }
        switch (e.getKeyChar()) {
            case 'a':
                nes.controllers_8[0] |= 0x20;
                break;
            case 's':
                nes.controllers_8[0] |= 0x10;
                break;
            case 'z':
                nes.controllers_8[0] |= 0x40;
            case 'x':
                nes.controllers_8[0] |= 0x80;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_UP:
                nes.controllers_8[0] &= ~0x08;
                break;
            case KeyEvent.VK_DOWN:
                nes.controllers_8[0] &= ~0x04;
                break;
            case KeyEvent.VK_LEFT:
                nes.controllers_8[0] &= ~0x02;
                break;
            case KeyEvent.VK_RIGHT:
                nes.controllers_8[0] &= ~0x01;
                break;
        }
        switch (e.getKeyChar()) {
            case  'a':
                nes.controllers_8[0] &= ~0x20;
                break;
            case 's':
                nes.controllers_8[0] &= ~0x10;
                break;
            case 'z':
                nes.controllers_8[0] &= ~0x40;
            case 'x':
                nes.controllers_8[0] &= ~0x80;
                break;
        }
    }

    private void runInstruction() {
        runningFullSpeed = false;
        do {
            nes.clock();
        } while (!nes.getCpu().isInstructionCompleted());
        leftPanel.repaint();
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
        frame.setPreferredSize(new Dimension(1483, 860));
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
    }

}