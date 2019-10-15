package nl.pouwels.nes;

import nl.pouwels.nes.cpu.EventPrinter;
import nl.pouwels.nes.cpu.Olc6502;
import nl.pouwels.nes.ppu.Olc2c02;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class NesTest {

    private static final String ACTUAL_LOGS_TXT = "./actual_logs.txt";

    @Before
    public void setUp() throws Exception {
        if (Files.exists(Paths.get(ACTUAL_LOGS_TXT))) {
            Files.delete(Paths.get(ACTUAL_LOGS_TXT));
        }
        Files.createFile(Paths.get(ACTUAL_LOGS_TXT));
    }

    @After
    public void tearDown() throws Exception {
        Files.delete(Paths.get(ACTUAL_LOGS_TXT));
    }

    @Test
    public void runNesTest() throws IOException {
        List<String> expectedLines = Files.readAllLines(Paths.get(getClass().getClassLoader().getResource("golden_log.txt").getPath()));
        Cartridge cartridge = new Cartridge(getClass().getClassLoader().getResource("nestest.nes").getPath());
        Olc6502 cpu = new Olc6502(new FileEventPrinter());
        Bus bus = new Bus(cpu, new Olc2c02());
        cpu.connectToBus(bus);
        bus.insertCartridge(cartridge);
        bus.reset(0xC000);
        int i = 0;
        while (i <= expectedLines.size()) {
            bus.clock();
            i++;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(ACTUAL_LOGS_TXT))) {
            for (int j = 0; j < expectedLines.size(); j++) {
                String line = reader.readLine();
                if (line == null) {
                    fail("Expected more lines");
                }
                assertEquals("Error at instruction " + (j + 1) + ": ", expectedLines.get(j).substring(0, 19), line.substring(0, 19));
                assertEquals("Error at instruction " + (j + 1) + ": ", expectedLines.get(j).substring(48, 73), line.substring(20, 45));
            }
        }
    }

    class FileEventPrinter extends EventPrinter {

        @Override
        protected void printInstructionLine(String line) {
            try (PrintWriter output = new PrintWriter(new FileWriter(ACTUAL_LOGS_TXT, true))) {
                output.printf("%s\r\n", line);
            } catch (Exception e) {
            }
        }
    }
}
