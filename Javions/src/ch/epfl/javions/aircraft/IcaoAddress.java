package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;


/**
 * L'enregistrement IcaoAddress représente une adresse OACI.
 * @param string adresse OACI de l'aéronef
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 *
 */
public record IcaoAddress(String string) {
    public static final Pattern GROUP_ICAO = Pattern.compile("[0-9A-F]{6}");

    public IcaoAddress{
        Preconditions.checkArgument(GROUP_ICAO.matcher(string).matches());
    }
}
