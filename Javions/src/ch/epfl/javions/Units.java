package ch.epfl.javions;

import static ch.epfl.javions.Units.Length.KILOMETER;
import static ch.epfl.javions.Units.Time.HOUR;
import static ch.epfl.javions.Units.Length.NAUTICAL_MILE;
import static java.lang.Math.PI;

/**
 * La classe Units contient la définition des préfixes SI (Système international) utiles au projet,
 * des classes imbriquées contenant les définitions des différentes unités,
 * ainsi que des méthodes de conversion.
 *
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */

public final class Units {

    /**
     * Constantes qui servent au changement de préfixe.
     */
    public static final double CENTI = 1e-2;
    public static final double KILO = 1e3;
    private Units(){}

    /**
     *
     * @param value valeur en unité de départ.
     * @param fromUnit unité de départ.
     * @param toUnit unité d'arrivée.
     * @return valeur transformée en unité d'arrivée.
     */
    public static double convert(double value, double fromUnit, double toUnit){
        return (value * (fromUnit/toUnit));
    }

    /**
     *
     * @param value valeur en unité de départ.
     * @param fromUnit unité de départ.
     * @return valeur en unité de base.
     */
    public static double convertFrom(double value, double fromUnit){
        return (value * fromUnit);
    }

    /**
     *
     * @param value valeur en unité de base.
     * @param toUnit unité d'arrivée.
     * @return unité de base en unité d'arrivée.
     */
    public static double convertTo(double value, double toUnit){
        return (value /toUnit);
    }

    /**
     * La classe empaquetée {@code Angle} contient les constantes qui servent au changement d'unités d'angle.
     */
    public static class Angle{
        public static final double RADIAN = 1d;
        public static final double TURN = 2d * PI * RADIAN;
        public static final double DEGREE = TURN / 360d;
        public static final double T32 = Math.scalb(TURN,-32);
        private Angle(){}

    }

    /**
     * La classe empaquetée {@code Length} contient les constantes qui servent au changement d'unités de longueur.
     */
    public static class Length{
        public static final double METER = 1;
        public static final double CENTIMETER = CENTI * METER;
        public static final double KILOMETER = KILO * METER;
        public static final double INCH = CENTIMETER * 2.54;
        public static final double FOOT = 12 * INCH;
        public static final double NAUTICAL_MILE = 1852 * METER;
        private Length(){}

    }

    /**
     * La classe empaquetée {@code Time} contient les constantes qui servent au changement d'unités de temps.
     */
    public static class Time{
        public static final double SECOND = 1;
        public static final double MINUTE = 60 * SECOND;
        public static final double HOUR = 60 * MINUTE;
        private Time(){}


    }

    /**
     * La classe empaquetée {@code Speed} contient les constantes qui servent au changement d'unités de vitesse.
     */
    public static class Speed{
        public static final double METER_PER_SECOND = 1;
        public static final double KNOT = METER_PER_SECOND * NAUTICAL_MILE / HOUR;
        public static final double KILOMETER_PER_HOUR = METER_PER_SECOND * KILOMETER / HOUR;
        private Speed(){}

    }
}
