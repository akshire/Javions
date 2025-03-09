package ch.epfl.javions;

/**
 * La classe Precondition offre une méthode de validation d'arguments
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */

public final class Preconditions {
    private Preconditions(){}

    /**
     *
     * @param shouldBeTrue à verifier.
     * @throws IllegalArgumentException lorsque le booléen passé en argument n'est pas respecté.
     */
    public static void checkArgument(boolean shouldBeTrue){
        if(!shouldBeTrue){throw new IllegalArgumentException("Precondition Non Satisfaite");}
    }
}
