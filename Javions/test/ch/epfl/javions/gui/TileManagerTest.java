package ch.epfl.javions.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public class TileManagerTest extends Application {
    public static void main(String[] args) { launch(args); }

    @Test
    @Override
    public void start(Stage primaryStage) throws Exception {
        TileManager tileManager = new TileManager(Path.of("tile-cache"),
                "tile.openstreetmap.org");
        Image img1 = tileManager.imageForTileAt(new TileManager.TileId(17, 67927, 46357));
        Image img2 = tileManager.imageForTileAt(new TileManager.TileId(17, 67927, 46357));



        for (int i = 0; i < img1.getWidth(); i++) {
            for (int j = 0; j < img1.getHeight(); j++) {
                if (img1.getPixelReader().getArgb(i, j) != img2.getPixelReader().getArgb(i, j)) System.out.println("PD");;
            }
        }

        Image img3 = tileManager.imageForTileAt(new TileManager.TileId(17, 67927, 46358));

        for(int i = 0 ; i<200 ; i++){
            Image img = tileManager.imageForTileAt(new TileManager.TileId(17,67927+i, 46358));
        }



        Platform.exit();

    }
}
