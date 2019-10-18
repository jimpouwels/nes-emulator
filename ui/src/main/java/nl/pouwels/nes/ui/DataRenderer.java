package nl.pouwels.nes.ui;

import nl.pouwels.nes.Bus;
import nl.pouwels.nes.cpu.Flag;
import nl.pouwels.nes.cpu.Olc6502;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

import static nl.pouwels.nes.utils.PrintUtilities.printAsHex;

public class DataRenderer {
    public static void drawData(List<Olc6502.InstructionAtAddress> instructionAtAddressList, JTextPane textPane, Bus nes) {
        try {
            int index = instructionAtAddressList.indexOf(instructionAtAddressList.stream().filter(i -> i.address == nes.getCpu().getProgramCounter_16()).collect(Collectors.toList()).get(0));
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
            doc.insertString(doc.getLength(), printAsHex(nes.getCpu().getProgramCounter_16(), 2), null);
            doc.insertString(doc.getLength(), "   ", null);
            doc.insertString(doc.getLength(), "A: ", registerStyle);
            doc.insertString(doc.getLength(), printAsHex((nes.getCpu().getAccumolatorRegister()), 2), null);
            doc.insertString(doc.getLength(), "   ", null);
            doc.insertString(doc.getLength(), "X: ", registerStyle);
            doc.insertString(doc.getLength(), printAsHex((nes.getCpu().getXRegister()), 2), null);
            doc.insertString(doc.getLength(), "   ", null);
            doc.insertString(doc.getLength(), "Y: ", registerStyle);
            doc.insertString(doc.getLength(), printAsHex((nes.getCpu().getYRegister()), 2), null);
            doc.insertString(doc.getLength(), "   ", null);
            doc.insertString(doc.getLength(), "SP: ", registerStyle);
            doc.insertString(doc.getLength(), printAsHex((nes.getCpu().getStackPointer()), 2), null);
            doc.insertString(doc.getLength(), "\n", null);

            doc.insertString(doc.getLength(), "\n", null);
            for (int i = -12; i < 12; i++) {
                if ((index + i) >= 0) {
                    String line = instructionAtAddressList.get(index + i).line;
                    Style style = null;
                    if (i == 0) {
                        style = textPane.addStyle(null, null);
                        StyleConstants.setForeground(style, Color.cyan);
                    }
                    doc.insertString(doc.getLength(), line + "\n", style);

                }
            }
            StyleConstants.setLineSpacing(textPane.getInputAttributes(), -2f);
        } catch (BadLocationException | IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
    }
}
