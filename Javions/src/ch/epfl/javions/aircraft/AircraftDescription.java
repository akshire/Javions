package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;
import java.util.regex.Pattern;


/**
 * L'enregistrement AircraftDescription représente la description d'un aéronef
 * @param string description de l'aéronef
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public record AircraftDescription(String string) {

    private static final Pattern GROUP_AIR_DESCRIPTION = Pattern.compile("[ABDGHLPRSTV-][0123468][EJPT-]");


    public AircraftDescription{
        Preconditions.checkArgument(GROUP_AIR_DESCRIPTION.matcher(string).matches()||string.isEmpty());
    }
}
