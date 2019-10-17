package nl.pouwels.nes.ui;

import nl.pouwels.nes.Bus;
import nl.pouwels.nes.cpu.Olc6502;
import nl.pouwels.nes.ppu.Color;
import nl.pouwels.nes.ppu.Screen;
import nl.pouwels.nes.ppu.Sprite;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.Collectors;

public class MainScreen extends JPanel implements Screen, KeyListener {

    private static final int SPACEBAR = 32;
    private static final String BACKGROUND_COLOR = "0x022f8e";
    private final BufferedImage gameCanvas;
    private BufferedImage patternTable1;
    private BufferedImage patternTable2;
    private Bus nes;
    private List<Olc6502.InstructionAtAddress> mapAsm;
    private JTextPane textPane = new JTextPane();

    public MainScreen() {
        renderWindow();
        renderStatus();
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();
        gameCanvas = new BufferedImage(341, 261, BufferedImage.TYPE_INT_RGB);
        textPane.setBounds(870, 250, 370, 390);
        Font f = new Font(Font.MONOSPACED, 0, 15);
        textPane.setFont(f);
        textPane.setForeground(java.awt.Color.WHITE);
        textPane.setFocusable(false);
        textPane.setVisible(true);
        textPane.setBackground(java.awt.Color.decode(BACKGROUND_COLOR));
        add(textPane);
        repaint();
    }

    public void setBus(Bus nes) {
        this.nes = nes;
        mapAsm = nes.getCpu().disassemble(0x0000, 0xFFFF);
        nes.reset();
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

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform imageSpaceTran = new AffineTransform();
        imageSpaceTran.scale(2.5f, 2.5f);
        g2.drawImage(gameCanvas, imageSpaceTran, null);
//        g2.drawImage(patternTable1, 500, 500, null);
//        g2.drawImage(patternTable2, 500, 500, null);
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
        setBackground(java.awt.Color.decode(BACKGROUND_COLOR));
        setLayout(null);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getExtendedKeyCode() == SPACEBAR) {
            do {
                nes.clock();
            } while (!nes.getCpu().isInstructionCompleted());
        }
        int index = mapAsm.indexOf(mapAsm.stream().filter(i -> i.address == nes.getCpu().getProgramCounter_16()).collect(Collectors.toList()).get(0));
        StyledDocument doc = textPane.getStyledDocument();
        textPane.setText("");
        for (int i = -10; i < 10; i++) {
            if ((index + i) >= 0) {
                try {
                    String line = mapAsm.get(index + i).line;
                    Style style = null;
                    if (i == 0) {
                        style = textPane.addStyle(null, null);
                        StyleConstants.setForeground(style, java.awt.Color.cyan);
                    }
                    doc.insertString(doc.getLength(), "\n" + line, style);
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private void drawCode(int nLines) {

    }
}