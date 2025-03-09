package ch.epfl.javions;

/**
 * La classe Math2 offre des méthodes statiques permettant d'effectuer certains calculs mathématiques.
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public final class Math2 {
    private Math2(){}

    /**
     *
     * @param min borne inférieure.
     * @param v valeur.
     * @param max borne supérieure.
     * @return valeur bornée entre min et max.
     * @throws IllegalArgumentException si min est plus grand ou égal que max
     */
    public static int clamp(int min, int v, int max) {
        Preconditions.checkArgument(min<=max);
        return Math.min(Math.max(min,v),max);
    }

    /**
     *
     * @param min borne inférieure
     * @param v valeur
     * @param max borne supérieure
     * @return la valeur bornée entre min et max
     * @throws IllegalArgumentException si min est plus grand ou égal que max
     */
    public static double clamp(double min, double v, double max){
        Preconditions.checkArgument(min<=max);
        return Math.min(max,Math.max(min,v));

    }

    /**
     *
     * @param x valeur.
     * @return arcsinh(x).
     */
    public static double asinh(double x){
        return Math.log(x + Math.hypot(1,x));
    }
}
