package ch.epfl.javions;


/**
 * La classe WebMercator contient des méthodes permettant de projeter
 * des coordonnées géographiques selon la projection WebMercator.
 * @author Erik Hübner (341205)
 */
public final class WebMercator {
    private WebMercator(){}

    /**
     *
     * @param zoomLevel niveau de zoom.
     * @param longitude longitude en radians.
     * @return coordonnée x en WebMercator.
     */
    public static double x(int zoomLevel, double longitude){
        return Math.scalb( 1 , 8 + zoomLevel ) * ( Units.convertTo( longitude , Units.Angle.TURN ) + 0.5);
    }

    /**
     *
     * @param zoomLevel niveau de zoom.
     * @param latitude latitude en radians.
     * @return coordonnée y en WebMercator.
     */
    public static double y (int zoomLevel, double latitude){
        return Math.scalb( 1 , 8 + zoomLevel ) * ( Units.convertTo( - ( Math2.asinh( Math.tan(latitude) ) ) , Units.Angle.TURN ) + 0.5 );
    }
}
