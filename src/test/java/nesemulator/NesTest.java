package nesemulator;

import nesemulator.cartridge.Cartridge;
import nesemulator.cpu.EventPrinter;
import nesemulator.cpu.Olc6502;
import nesemulator.ppu.Olc2c02;
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
//        Files.delete(Paths.get(ACTUAL_LOGS_TXT));
    }

    @Test
    public void runNesTest() throws IOException {
        List<String> expectedLines = Files.readAllLines(Paths.get(getClass().getClassLoader().getResource("golden_log.txt").getPath()));
        Cartridge cartridge = new Cartridge("./nestest.nes");
        Olc6502 cpu = new Olc6502(new FileEventPrinter());
        Bus bus = new Bus(cpu, new Olc2c02());
        cpu.connectToBus(bus);
        bus.insertCartridge(cartridge);
        bus.reset();
        int i = 0;
        while (i++ < expectedLines.size()) {
            bus.clock();
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(ACTUAL_LOGS_TXT))) {
            for (int j = 0; j < expectedLines.size(); j++) {
                String line = reader.readLine();
                if (line == null) {
                    fail("Expected more lines");
                }
                assertEquals("Error at instruction " + (j + 1) + ": ", expectedLines.get(j).substring(0, 19), line.substring(0, 19));
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
