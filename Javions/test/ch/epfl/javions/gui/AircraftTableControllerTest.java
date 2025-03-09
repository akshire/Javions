package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public final class AircraftTableControllerTest extends Application {
    public static void main(String[] args) {launch(args); }

    static List<RawMessage> readAllMessages(String fileName)throws IOException {
        List<RawMessage> list = new ArrayList<>();
        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(fileName)))){
            byte[] bytes = new byte[RawMessage.LENGTH];
            while (true) {
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                ByteString message = new ByteString(bytes);
                list.add(new RawMessage(timeStampNs,message));

            }
        } catch (EOFException e) { /* nothing to do */ }
        return list;

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // … à compléter (voir TestBaseMapController)
        Path tileCache = Path.of("tile-cache");
        TileManager tileManager =
                new TileManager(tileCache, "tile.openstreetmap.org");
        MapParameters mapParameters =
                new MapParameters(17, 17_389_327, 11_867_430);
        BaseMapController baseMapController = new BaseMapController(tileManager, mapParameters);

        // Création de la base de données
        URL dbUrl = getClass().getResource("/aircraft.zip");
        assert dbUrl != null;
        String f = Path.of(dbUrl.toURI()).toString();
        var database = new AircraftDatabase(f);

        AircraftStateManager aircraftStateManager = new AircraftStateManager(database);
        ObjectProperty<ObservableAircraftState> selectedAircraft =
                new SimpleObjectProperty<>();



        AircraftTableController aircraftTableController = new AircraftTableController(aircraftStateManager.states(),selectedAircraft);
        //AircraftController ac =
           //     new AircraftController(mp, asm.states(), sap);

        var root = new StackPane(baseMapController.pane(), aircraftTableController.pane());
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        var mi = readAllMessages("resources/messages_20230318_0915.bin")
                .iterator();

        // Animation des aéronefs
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    for (int i = 0; i < 10; i += 1) {
                        Message m = MessageParser.parse(mi.next());
                        if (m != null) {
                            aircraftStateManager.updateWithMessage(m);

                        }
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }
}
