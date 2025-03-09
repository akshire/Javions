package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;



/**
 * L'enregistrement CallSign représente ce que l'on nomme l'indicatif d'un aéronef.
 * @param string
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public record CallSign(String string) {
    private static final Pattern GROUP_CALL_SIGN = Pattern.compile("[A-Z0-9 ]{0,8}");

    public CallSign{
        Preconditions.checkArgument(GROUP_CALL_SIGN.matcher(string).matches() || string.isEmpty());
    }
}
