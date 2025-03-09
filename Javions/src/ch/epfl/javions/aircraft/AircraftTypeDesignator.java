package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;



/**
 * L'enregistrement AircraftTypeDesignator représente l'indicateur de type d'un aéronef.
 * @param string type de l'aéronef
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public record AircraftTypeDesignator(String string) {
    private static final Pattern GROUP_AIR_TYPE = Pattern.compile("[A-Z0-9]{2,4}");

    public AircraftTypeDesignator{
        Preconditions.checkArgument(GROUP_AIR_TYPE.matcher(string).matches()||string.isEmpty());
    }
}
