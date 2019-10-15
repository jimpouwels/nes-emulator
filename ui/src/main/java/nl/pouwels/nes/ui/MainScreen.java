package nl.pouwels.nes.ui;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import nl.pouwels.nes.ppu.Pixel;
import nl.pouwels.nes.ppu.Screen;

public class MainScreen implements Screen {

    private PixelWriter pw;

    public void show(Stage primaryStage) {
        StackPane root = new StackPane();
        primaryStage.setTitle("NES Emulator");
        primaryStage.setScene(new Scene(root, 1600, 900));
        primaryStage.show();
        Canvas canvas = new Canvas(1600, 900);

        root.getChildren().add(canvas);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        pw = gc.getPixelWriter();
    }

    @Override
    public void drawPixel(int cycle, int scanline, Pixel pixel) {
        pw.setColor(cycle, scanline, Color.rgb(pixel.r, pixel.g, pixel.b));
    }
}