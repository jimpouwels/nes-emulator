package nl.pouwels.nes.main;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import nl.pouwels.nes.Bus;
import nl.pouwels.nes.Cartridge;
import nl.pouwels.nes.InstructionPrinter;
import nl.pouwels.nes.cpu.Olc6502;
import nl.pouwels.nes.ppu.Olc2c02;

public class Main extends javafx.application.Application {

    public static void main(String... args) {
        System.out.println("Starting Jim's NES Emulator!");
        System.out.println("-----------------------------");
        launch();
        Cartridge cartridge = new Cartridge(Main.class.getClassLoader().getResource("nestest.nes").getPath());
        Olc6502 cpu = new Olc6502(new InstructionPrinter());
        Bus bus = new Bus(cpu, new Olc2c02());
        cpu.connectToBus(bus);
        bus.insertCartridge(cartridge);
        System.out.println("Starting testrom...");
        bus.reset(0xC000);
        int i = 0;
        while (true) {
            bus.clock();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();
        primaryStage.setTitle("NES Emulator");
        primaryStage.setScene(new Scene(root, 1600, 900));
        primaryStage.show();
        BorderPane border = new BorderPane();
        root.getChildren().add(border);
    }
}
