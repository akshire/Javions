package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import ch.epfl.javions.Preconditions;
import javafx.beans.property.*;

/**
 * La classe MapParameters représente les paramètres de la portion de la carte visible dans l'interface graphique
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public final class MapParameters {
    private static final int MIN_ZOOM = 6;
    private static final int MAX_ZOOM = 19;
    private final IntegerProperty zoomProperty;
    private final DoubleProperty minXProperty, minYProperty;



    /**
     * Constructeur
     *
     * @param zoom niveau de zoom
     * @param minX coordonnée x en coordonnées WebMercator
     * @param minY coordonnée y en coordonnées WebMercator
     */
    public MapParameters(int zoom, int minX, int minY){
        Preconditions.checkArgument(MIN_ZOOM <= zoom && zoom <= MAX_ZOOM);
        this.zoomProperty = new SimpleIntegerProperty(zoom);
        this.minXProperty = new SimpleDoubleProperty(minX);
        this.minYProperty = new SimpleDoubleProperty(minY);
    }

    /**
     * Translate minX et minY de la valeur du vecteur donné
     *
     * @param x composante x du vecteur
     * @param y composante y du vecteur
     */
    public void scroll(double x, double y){
        this.minXProperty.set(this.minXProperty.doubleValue() + x);
        this.minYProperty.set(this.minYProperty.doubleValue() + y);
    }


    /**
     * Change le niveau de zoom en additionnant avec la valeur donnée
     *
     * @param addingDelta delta Niveau de zoom à ajouter.
     */
    public void changeZoom(int addingDelta){
        int power = Math2.clamp(MIN_ZOOM- zoomProperty.get(),addingDelta,MAX_ZOOM - zoomProperty.get());
        this.minXProperty.set(this.minXProperty.doubleValue() * Math.scalb(1, power));
        this.minYProperty.set(this.minYProperty.doubleValue() * Math.scalb(1, power));
        this.zoomProperty.set(Math2.clamp(MIN_ZOOM, this.zoomProperty.get() + addingDelta,MAX_ZOOM));

    }

    /**
     * Getter de la propriété du zoom
     *
     * @return la propriété du zoom en lecture seule
     */
    public ReadOnlyIntegerProperty zoomProperty(){return zoomProperty;}

    /**
     * Getter de la valeur du zoom
     *
     * @return la valeur du zoom
     */
    public int getZoom(){return zoomProperty.get();}

    /**
     * Setter pour la propriété minX
     *
     * @param x nouvelle valeur de minX
     */
    public void setMinX(double x){minXProperty.set(x);}

    /**
     * Getter de la propriété de minX
     *
     * @return la propriété minX en lecture seule
     */
    public ReadOnlyDoubleProperty minXProperty(){return minXProperty;}

    /**
     * Getter de la valeur de minX
     *
     * @return la valeur de minX
     */
    public double getMinX(){return minXProperty.getValue();}


    /**
     * Setter pour la propriété minY
     *
     * @param y nouvelle valeur de minY
     */
    public void setMinY(double y){minYProperty.set(y);}

    /**
     * Getter de la propriété de minY
     *
     * @return la valeur de minY en lecture seule
     */
    public ReadOnlyDoubleProperty minYProperty(){return minYProperty;}

    /**
     * Getter de la valeur de minY
     *
     * @return la valeur de minY
     */
    public double getMinY(){return minYProperty.getValue();}
}
