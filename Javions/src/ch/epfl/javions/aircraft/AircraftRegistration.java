package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;


/**
 * L'enregistrement AircraftRegistration représente l'immatriculation d'un aéronef.
 * @param string immatriculation de l'aéronef
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public record AircraftRegistration(String string) {
    private static final Pattern GROUP_AIR_REGISTRATION = Pattern.compile("[A-Z0-9 .?/_+-]+");

    public AircraftRegistration{
        Preconditions.checkArgument(GROUP_AIR_REGISTRATION.matcher(string).matches());
    }
}
