package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import static java.lang.Thread.sleep;


/**
 * La classe Main contient le programme principal
 *
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public final class Main extends Application {
    private static final String SERVER_NAME = "tile.openstreetmap.org";
    private static final String PATH_NAME = "tile-cache";
    private static final int ZOOM_START = 8;
    private static final int MINX_START = 33_530;
    private static final int MINY_START = 23_070;
    private static final String PATH_OF_DATABASE_IN_RESOURCE = "/aircraft.zip";
    private static final long ONE_SECOND_IN_NANOSECOND = 1_000_000_000L;
    private static final long ONE_MILLISECOND_IN_NANOSECOND = 1_000_000L;
    private long currentTimeStamps = 0;

    /**
     * Méthode ne servant qu'à appeler launch
     *
     * @param args arguments
     */
    public static void main(String[] args) {launch(args);}

    /**
     * Méthode servant à démarrer l'application en construisant le graphe de scène correspondant à l'interface
     * graphique, démarrant le fil d'exécution chargé d'obtenir les messages, et enfin démarrant
     * le « minuteur d'animation » chargé de mettre à jour les états d'aéronefs en fonction des messages reçus.
     *
     * @param primaryStage graphe de scène principal
     */
    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("Javions");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        ConcurrentLinkedQueue<RawMessage> extractingMessageQueue = new ConcurrentLinkedQueue<>();

           Path tileCache = Path.of(PATH_NAME);
           TileManager tileManager =
                   new TileManager(tileCache, SERVER_NAME);
           MapParameters mapParameters =
                   new MapParameters(ZOOM_START, MINX_START, MINY_START);
           BaseMapController baseMapController = new BaseMapController(tileManager, mapParameters);

           URL u = getClass().getResource(PATH_OF_DATABASE_IN_RESOURCE);
           assert u != null;
           Path p;
           try {
               p = Path.of(u.toURI());
           } catch (URISyntaxException e) {
               throw new RuntimeException(e);
           }
           AircraftDatabase database = new AircraftDatabase(p.toString());


           AircraftStateManager aircraftStateManager = new AircraftStateManager(database);
           ObjectProperty<ObservableAircraftState> selectedAircraft =
                   new SimpleObjectProperty<>();

           AircraftTableController aircraftTableController = new AircraftTableController
                   (aircraftStateManager.states(),selectedAircraft);

           AircraftController aircraftController =
                   new AircraftController(mapParameters, aircraftStateManager.states(), selectedAircraft);

           StatusLineController sl = new StatusLineController();
           sl.aircraftCountProperty().bind(Bindings.size(aircraftStateManager.states()));


           BorderPane backGround = new BorderPane();
           backGround.setCenter(aircraftTableController.pane());
           backGround.setTop(sl.pane());

           StackPane stackPane = new StackPane(baseMapController.pane(),aircraftController.pane());
           SplitPane mainPane = new SplitPane(stackPane,backGround);
           mainPane.setOrientation(Orientation.VERTICAL);

           aircraftTableController.setOnDoubleClick(observableAircraftState ->
                   baseMapController.centerOn(observableAircraftState.getPosition())
           );

           primaryStage.setScene(new Scene(mainPane));
           primaryStage.show();

           new AnimationTimer() {
           @Override
           public void handle(long now) {
               try {

                   long count = 0;
                   for (RawMessage raw : extractingMessageQueue) {

                       if(Objects.isNull(raw))throw new EOFException();
                       Message message = MessageParser.parse(raw);

                       if (message != null) {
                           aircraftStateManager.updateWithMessage(message);
                           count += 1;
                       }
                       extractingMessageQueue.remove(raw);
                   }

                   if(now - currentTimeStamps >= ONE_SECOND_IN_NANOSECOND){
                       currentTimeStamps = now;
                       aircraftStateManager.purge();
                   }

                   sl.messageCountProperty().set(sl.messageCountProperty().getValue() + count);

               } catch (IOException e) {
                   throw new UncheckedIOException(e);
               } catch (Exception e) {
                   throw new RuntimeException(e);
               }
           }
       }.start();
        Thread thread = new Thread(() -> {
            AdsbDemodulator adsbDemodulator;
            if(getParameters().getRaw().size() != 0) {
                try{
                    List<RawMessage> data = readAllMessages(getParameters().getRaw().get(0));
                    Iterator<RawMessage> iterRawTor = data.iterator();
                    long startingTime = System.nanoTime();
                    long elapsedTime;

                    RawMessage message = iterRawTor.next();

                    long firstTimeStamps = message.timeStampNs();
                    extractingMessageQueue.add(message);

                    while(iterRawTor.hasNext()){
                        message = iterRawTor.next();
                        elapsedTime = System.nanoTime() - startingTime;
                        if(elapsedTime < (message.timeStampNs() - firstTimeStamps)){
                            sleep(((message.timeStampNs() - firstTimeStamps) -
                                    elapsedTime)/ONE_MILLISECOND_IN_NANOSECOND);
                        }
                        extractingMessageQueue.add(message);

                    }

                }catch (IOException | InterruptedException e){
                    throw new RuntimeException();
                }
            }else{
                try(System.in) {
                    adsbDemodulator = new AdsbDemodulator(System.in);
                    while (true) {
                        extractingMessageQueue.add(adsbDemodulator.nextMessage());
                    }
                }catch (NullPointerException exception){
                    /* fin des messages */
                }catch (IOException e){
                    throw new RuntimeException();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Méthode qui lit un fichier,
     * puis retourne une liste de RawMessages
     * créées à l'aide des octets contenu au sein de ce même fichier
     *
     * @param fileName nom du fichier
     * @return Une Liste de RawMessages
     * @throws IOException si il y'a un problème avec le stream
     */

    private static List<RawMessage> readAllMessages(String fileName)throws IOException{
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
        } catch (EOFException e) { /* Fin du ficher */ }
        return list;
    }
}

