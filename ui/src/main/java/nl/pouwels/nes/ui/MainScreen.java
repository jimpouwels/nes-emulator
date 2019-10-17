package nl.pouwels.nes.ui;

import nl.pouwels.nes.Bus;
import nl.pouwels.nes.cpu.Flag;
import nl.pouwels.nes.cpu.Olc6502;
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

    private static final String BACKGROUND_COLOR = "0x022f8e";
    private final BufferedImage gameCanvas;
    private Bus nes;
    private List<Olc6502.InstructionAtAddress> mapAsm;
    private JTextPane textPane = new JTextPane();

    public MainScreen() {
        renderWindow();
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();
        gameCanvas = new BufferedImage(256, 240, BufferedImage.TYPE_INT_RGB);
        drawInfo();
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
        mapAsm = nes.getCpu().disassemble(0x0000, 0xFFFF);
        nes.reset();
    }

    @Override
    public void drawPixel(int x, int y, nl.pouwels.nes.ppu.Color color) {
        drawPixel(gameCanvas, x, y, color);
        if (x == 340 && y == 240) {
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

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == ' ') {
            do {
                nes.clock();
            } while (!nes.getCpu().isInstructionCompleted());
        }
        try {
            int index = mapAsm.indexOf(mapAsm.stream().filter(i -> i.address == nes.getCpu().getProgramCounter_16()).collect(Collectors.toList()).get(0));
            StyledDocument doc = textPane.getStyledDocument();
            textPane.setText("");

            Style statusStyle = textPane.addStyle(null, null);
            StyleConstants.setBold(statusStyle, true);
            doc.insertString(doc.getLength(), "STATUS:  ", statusStyle);

            Style flagStyle = textPane.addStyle(null, null);
            StyleConstants.setBold(flagStyle, true);
            StyleConstants.setForeground(flagStyle, nes.getCpu().getFlag(Flag.CARRY) == 1 ? Color.decode("0x008000") : Color.decode("0xB22222"));
            doc.insertString(doc.getLength(), "C", flagStyle);
            doc.insertString(doc.getLength(), "  ", statusStyle);
            StyleConstants.setForeground(flagStyle, nes.getCpu().getFlag(Flag.ZERO) == 1 ? Color.decode("0x008000") : Color.decode("0xB22222"));
            doc.insertString(doc.getLength(), "Z", flagStyle);
            doc.insertString(doc.getLength(), "  ", statusStyle);
            StyleConstants.setForeground(flagStyle, nes.getCpu().getFlag(Flag.DISABLE_INTERRUPTS) == 1 ? Color.decode("0x008000") : Color.decode("0xB22222"));
            doc.insertString(doc.getLength(), "I", flagStyle);
            doc.insertString(doc.getLength(), "  ", statusStyle);
            StyleConstants.setForeground(flagStyle, nes.getCpu().getFlag(Flag.DECIMAL_MODE) == 1 ? Color.decode("0x008000") : Color.decode("0xB22222"));
            doc.insertString(doc.getLength(), "D", flagStyle);
            doc.insertString(doc.getLength(), "  ", statusStyle);
            StyleConstants.setForeground(flagStyle, nes.getCpu().getFlag(Flag.BREAK) == 1 ? Color.decode("0x008000") : Color.decode("0xB22222"));
            doc.insertString(doc.getLength(), "B", flagStyle);
            doc.insertString(doc.getLength(), "  ", statusStyle);
            StyleConstants.setForeground(flagStyle, nes.getCpu().getFlag(Flag.UNUSED) == 1 ? Color.decode("0x008000") : Color.decode("0xB22222"));
            doc.insertString(doc.getLength(), "U", flagStyle);
            doc.insertString(doc.getLength(), "  ", statusStyle);
            StyleConstants.setForeground(flagStyle, nes.getCpu().getFlag(Flag.OVERFLOW) == 1 ? Color.decode("0x008000") : Color.decode("0xB22222"));
            doc.insertString(doc.getLength(), "O", flagStyle);
            doc.insertString(doc.getLength(), "  ", statusStyle);
            StyleConstants.setForeground(flagStyle, nes.getCpu().getFlag(Flag.NEGATIVE) == 1 ? Color.decode("0x008000") : Color.decode("0xB22222"));
            doc.insertString(doc.getLength(), "N", flagStyle);

            doc.insertString(doc.getLength(), "\n", null);
            Style registerStyle = textPane.addStyle(null, null);
            StyleConstants.setBold(registerStyle, true);
            doc.insertString(doc.getLength(), "PC: ", registerStyle);
            doc.insertString(doc.getLength(), Integer.toString(nes.getCpu().getProgramCounter_16()), null);
            doc.insertString(doc.getLength(), "\n", null);
            doc.insertString(doc.getLength(), "A: ", registerStyle);
            doc.insertString(doc.getLength(), Integer.toString(nes.getCpu().getAccumolatorRegister()), null);
            doc.insertString(doc.getLength(), "\n", null);
            doc.insertString(doc.getLength(), "X: ", registerStyle);
            doc.insertString(doc.getLength(), Integer.toString(nes.getCpu().getXRegister()), null);
            doc.insertString(doc.getLength(), "\n", null);
            doc.insertString(doc.getLength(), "Y: ", registerStyle);
            doc.insertString(doc.getLength(), Integer.toString(nes.getCpu().getYRegister()), null);
            doc.insertString(doc.getLength(), "\n", null);
            doc.insertString(doc.getLength(), "SP: ", registerStyle);
            doc.insertString(doc.getLength(), Integer.toString(nes.getCpu().getStackPointer()), null);
            doc.insertString(doc.getLength(), "\n", null);

            doc.insertString(doc.getLength(), "\n\n", null);
            for (int i = -13; i < 13; i++) {
                if ((index + i) >= 0) {
                    String line = mapAsm.get(index + i).line;
                    Style style = null;
                    if (i == 0) {
                        style = textPane.addStyle(null, null);
                        StyleConstants.setForeground(style, Color.cyan);
                    }
                    doc.insertString(doc.getLength(), line + "\n", style);

                }
            }
            repaint();
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }

    private void drawInfo() {
        textPane.setBounds(780, 10, 370, 630);
        Font f = new Font(Font.MONOSPACED, 0, 15);
        textPane.setFont(f);
        textPane.setForeground(Color.WHITE);
        textPane.setFocusable(false);
        textPane.setVisible(true);
        textPane.setBackground(Color.decode(BACKGROUND_COLOR));
        add(textPane);
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}