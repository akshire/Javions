package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;



import ch.epfl.javions.WebMercator;
import javafx.application.Platform;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;


import java.io.IOException;


/**
 * La classe BaseMapController gère l'affichage et l'interaction avec le fond de carte
 *
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public final class BaseMapController {
    private static final int TILE_PIXEL_SIZE = 256;
    TileManager tileManager;
    MapParameters mapParameters;
    private boolean redrawNeeded;
    LongProperty minScrollTime;
    private final Pane pane;
    private final Canvas canvas;
    private final GraphicsContext graphicsContext;

    //propriétés qui sauvegardent la position de la souris
    private final DoubleProperty referenceX,referenceY;

    /**
     * Constructeur
     * @param tileManager gestionnaire de tuile OSM
     *
     * @param mapParameters paramètres de la portion visible de la carte
     */
    public BaseMapController(TileManager tileManager, MapParameters mapParameters){
        this.tileManager = tileManager;
        this.mapParameters = mapParameters;
        this.canvas = new Canvas();
        this.graphicsContext = canvas.getGraphicsContext2D();
        this.pane  = new Pane();
        this.minScrollTime = new SimpleLongProperty();
        this.referenceX = new SimpleDoubleProperty();
        this.referenceY = new SimpleDoubleProperty();


        canvas.heightProperty().bind(pane.heightProperty());
        canvas.widthProperty().bind(pane.widthProperty());
        pane.getChildren().add(canvas);


        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
        canvas.widthProperty().addListener(value -> redrawOnNextPulse());
        canvas.heightProperty().addListener(value -> redrawOnNextPulse());



        pane.setOnScroll(e -> {
            int zoomDelta = (int) Math.signum(e.getDeltaY());
            if (zoomDelta == 0) return;

            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);


            mapParameters.scroll(e.getX(),e.getY());
            mapParameters.changeZoom(zoomDelta);
            mapParameters.scroll(-e.getX(),-e.getY());


        });


        pane.setOnMousePressed(mouseEvent -> {  referenceX.set(mouseEvent.getX());
                                                referenceY.set(mouseEvent.getY());});

        pane.setOnMouseDragged(event -> {
            mapParameters.setMinX(mapParameters.getMinX() + (referenceX.doubleValue() - event.getX()));
            mapParameters.setMinY(mapParameters.getMinY() + (referenceY.doubleValue() - event.getY()));


            referenceX.set(event.getX());
            referenceY.set(event.getY());
            });

        mapParameters.zoomProperty().addListener(event -> redrawOnNextPulse());
        mapParameters.minXProperty().addListener(event -> redrawOnNextPulse());
        mapParameters.minYProperty().addListener(event -> redrawOnNextPulse());



    }

    /**
     * Getter du panneau contenant toutes les tuiles visibles
     *
     * @return  le panneau JavaFX affichant le fond de carte
     */
    public Pane pane(){
        return pane;
    }

    /**
     * Méthode servant à déplacer la portion visible de la carte afin qu'elle soit centrée en ce point
     *
     * @param point un point à la surface de la Terre
     */
    public void centerOn(GeoPos point){
        double x = WebMercator.x(mapParameters.getZoom(), point.longitude());
        double y = WebMercator.y(mapParameters.getZoom(), point.latitude());
        mapParameters.setMinX(x - canvas.getWidth()/2);
        mapParameters.setMinY(y - canvas.getHeight()/2);

    }

    /**
     * Méthode servant à redessiner la carte visible dans la mesure où c'est nécessaire.
     */
    private void redrawIfNeeded(){
        if (!redrawNeeded) return;
        redrawNeeded = false;
        int startX = (int)(mapParameters.getMinX()/256);
        int startY = (int)(mapParameters.getMinY()/256);
        int differenceY = (int)(mapParameters. getMinY() - (startY * 256));
        int differenceX = (int)(mapParameters.getMinX() - (startX * 256));
        graphicsContext.clearRect(0,0, canvas.getWidth(), canvas.getHeight());
        int count = 0;
        for (int y = 0 ; y < canvas.getHeight()+TILE_PIXEL_SIZE ; y += 256) {
            for (int x = 0 ; x < canvas.getWidth()+TILE_PIXEL_SIZE ; x += 256) {
                try {
                    TileManager.TileId tileId;
                    if (TileManager.TileId.isValid(mapParameters.getZoom(),startX+count ,startY)){
                        tileId = new TileManager.TileId(mapParameters.getZoom(), startX+count ,startY);
                        graphicsContext.drawImage(tileManager.imageForTileAt(tileId),x - differenceX,y - differenceY);
                    }
                    count += 1;
                }catch (IOException ignored){}
            }
            count = 0;
            startY += 1;
        }
    }


    /**
     * Méthode forçant JavaFX à effectuer le prochain battement, même si de son point de vue cela n'est pas nécessaire
     */
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

}
