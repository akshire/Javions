package ch.epfl.javions.aircraft;


/**
 * Le type énuméré WakeTurbulenceCategory représente la catégorie de turbulence de sillage d'un aéronef.
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public enum WakeTurbulenceCategory {

    //catégories de turbulence de sillage
    LIGHT,
    MEDIUM,
    HEAVY,
    UNKNOWN;

    /**
     *
     * @param s lettre majuscule correspondant à la catégorie de turbulence de sillage.
     * @return la catégorie de turbulence de sillage.
     */
    public static WakeTurbulenceCategory of(String s){
        return switch (s) {
            case "L" -> LIGHT;
            case "M" -> MEDIUM;
            case "H" -> HEAVY;
            default -> UNKNOWN;
        };
    }
}
