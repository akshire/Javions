package ch.epfl.javions;

import java.util.Objects;

/**
 * La classe Bits contient des méthodes permettant d'extraire un sous-ensemble des 64 bits
 * d'une valeur de type {@code long}.
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */

public final class Bits {
    private Bits(){}

    /**
     *
     * @param value valeur du long
     * @param start index de départ de la chaîne à extraire (index 0 étant le bit de poids faible)
     * @param size longueur de la chaîne de bits à extraire
     * @return la chaîne de bits extraite de manière non-signée.
     * @throws IllegalArgumentException si la size se situe entre 0 (exclu) et 32 (exclu)
     * @throws IndexOutOfBoundsException si l'index + start n'est pas valide
     */
    public static int extractUInt(long value, int start, int size){
        Preconditions.checkArgument(size > 0 && size < Integer.SIZE);
        Objects.checkFromIndexSize(start, size, Long.SIZE);

        long moveLeft = value << Long.SIZE - size - start;
        long moveRight = moveLeft >>> Long.SIZE - size;

        return (int)moveRight;
    }

    /**
     *
     * @param value valeur de la chaîne de bits
     * @param index index du bit dont on veut connaître la valeur (0 poids faible, 63 MSB)
     * @return {@code true} si le bit vaut 1, {@code false} si le bit vaut 0
     * @throws IndexOutOfBoundsException si l'index ne se trouve pas dans les bonnes bornes
     */
    public static boolean testBit(long value, int index) {
        Objects.checkIndex(index, Long.SIZE);

        long moveLeft = value << Long.SIZE - 1 - index;
        long moveRight = moveLeft >>> Long.SIZE - 1;

        return moveRight == 1;
    }
}
