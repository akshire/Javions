package ch.epfl.javions;

/**
 * L'enregistrement GeoPos représente des coordonnées géographiques, c.-à-d. un couple longitude/latitude.
 * Ces coordonnées sont exprimées en t32 et stockées sous la forme d'entiers de 32 bits (type int).
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public record GeoPos(int longitudeT32, int latitudeT32) {
    /**
     *
     * @param longitudeT32 longitude exprimée en unité T32.
     * @param latitudeT32  latitude exprimée en unité T32.
     */
    public GeoPos {Preconditions.checkArgument(isValidLatitudeT32(latitudeT32));}

    /**
     * Méthode de permettant de vérifier la validité de la latitude reçue.
     * @param latitudeT32 coordonnée en T32.
     * @return True si la latitude est valide.
     */
    public static boolean isValidLatitudeT32(int latitudeT32) {
        return latitudeT32 >= -Math.pow(2, 30) && latitudeT32 <= Math.pow(2,30);
    }

    /**
     * Convertit T32 en radian.
     * @return la longitude en radian.
     */
    public double longitude(){
        return Units.convertFrom(longitudeT32,Units.Angle.T32);
    }

    /**
     * Convertit T32 en radian.
     * @return la latitude en radian.
     */
    public double latitude(){
        return Units.convertFrom(latitudeT32,Units.Angle.T32);
    }

    /**
     *
     * @return les coordonnées sous forme de {@code String}.
     */
    @Override
    public String toString(){
        return "(" + Units.convert(longitudeT32,Units.Angle.T32,Units.Angle.DEGREE) + "°, "
                + Units.convert(latitudeT32,Units.Angle.T32,Units.Angle.DEGREE) + "°)";
    }
}
