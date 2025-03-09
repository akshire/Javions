package ch.epfl.javions;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

/**
 * La classe ByteString représente une chaîne (séquence) d'octets interprétés de manière non-signée.
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */

public final class ByteString {


    /** Attribut représentant une liste de {@code byte} interprétés de manière non signée. */
    private final byte[] bytes;
    public ByteString(byte[] bytes){
        this.bytes = bytes.clone();
    }

    /**
     * Méthode {@code static} permettant de transformer une valeur de type {@code String} en chaîne de {@code byte}.
     * @param hexString {@code String} représentant la chaîne de {@code byte} en hexadecimal.
     * @return tableau contenant les valeurs de type {@code byte}.
     * @throws IllegalArgumentException si la longueur n'est pas pair
     */
    public static ByteString ofHexadecimalString(String hexString){
        Preconditions.checkArgument(hexString.length() % 2 == 0);
        HexFormat hf = HexFormat.of().withUpperCase();
        byte[] byt = hf.parseHex(hexString);
        return new ByteString(byt);
    }


    /**
     *
     * @return la longueur de la chaine de {@code byte}
     */
    public int size(){
        return bytes.length;
    }


    /**
     * Retourne la valeur de type {@code byte} du tableau stocké en attribut.
     * @param index du {@code byte} que l'on souhaite récupérer.
     * @return la valeur de type {@code byte}
     * @throws IndexOutOfBoundsException si l'index ne se situe pas dans les bonnes bornes
     */
    public int byteAt(int index){
        Objects.checkIndex(index,bytes.length);
        //transformation en octets non signés
        return bytes[index] >= 0 ? bytes[index] : bytes[index] & 0xff;
    }


    /**
     * Cette méthode extrait, du tableau stocké en attribut, une chaîne de {@code byte} sous la forme d'un {@code long}.
     * @param fromIndex est l'index de départ de la chaîne à extraire.(0 étant l'octet de poids fort)
     * @param toIndex est l'index d'arrivée dont la valeur est exclue de l'extraction.
     * @return la chaîne extraite sous la forme d'un {@code long}.
     * @throws IndexOutOfBoundsException si l'index ne se situe pas dans les bonnes bornes
     * @throws IllegalArgumentException si fromIndex est plus grand ou égal que toIndex
     */
    public long bytesInRange(int fromIndex, int toIndex){
        Preconditions.checkArgument(fromIndex<=toIndex);
        Objects.checkFromIndexSize(fromIndex, toIndex - fromIndex , bytes.length);

        long retour = 0;

        for(int i = fromIndex; i < toIndex ; ++i) {
            retour = retour << Byte.SIZE;
            retour = retour | this.byteAt(i);

        }
        return retour;
    }


    /**
     * Méthode de comparaison.
     * @param b objet avec lequel on souhaite comparer celui-ci.
     * @return {@code true} si les objets sont les mêmes ,{@code false} sinon.
     */
    @Override
    public boolean equals(Object b){
        return b instanceof ByteString that && Arrays.equals(that.bytes, this.bytes);
    }

    /**
     * Méthode retournant le HashCode du tableau de {@code byte}.
     * @return hashCode.
     */
    @Override
    public int hashCode(){
        return Arrays.hashCode(bytes);
    }


    /**
     * Méthode qui transforme le tableau de {@code byte} en {@code String}.
     * @return {@code String} du tableau de {@code byte}.
     */
    @Override
    public String toString(){
        HexFormat hf = HexFormat.of().withUpperCase();
        return hf.formatHex(bytes);
    }
}
