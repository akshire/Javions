package ch.epfl.javions;

/**
 * La classe Crc24 représente un calculateur de CRC de 24 bits.
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong
 */

public final class Crc24 {

    //24 bits de poids faible du générateur utilisé pour calculer le CRC24
    public static final int GENERATOR = 0xFFF409;
    //nombre de bits dans le crc
    private static final int N = 24;
    //taille du tableau
    private static final int TABLE_LENGTH = 256;
    private final int[] geneTable;

    /**
     * Prend le générateur et en fait une table, pour pouvoir ensuite calculer le CRC,
     * avec l'algorithme le plus optimisé.
     * @param generator qui va être le générateur pour calculer le CRC24
     */
    public Crc24(int generator){
        this.geneTable = buildTable(generator);
    }

    /**
     *
     * @param bytes Est le tableau contenant le message.
     * La méthode permet de calculer le CRC de 24 bits du message correspondant contenu dans le tableau de bytes en argument.
     * @return le CRC24 du tableau passé en argument.
     */
    public int crc(byte[] bytes) {
        return crc_bitwiseOPTIMAX(bytes);
    }


    private static int crc_bitwise(int generator, byte[] table){
        int crc = 0;
        int gPrime = Bits.extractUInt(generator,0,N);
        int[] tableNew = {0,gPrime};

        for (byte b:table) {
            for(int j = 7; j >= 0;--j){
                crc = ((crc << 1) | (Bits.testBit(b,j) ? 1 : 0)) ^ tableNew[(Bits.testBit(crc,N-1) ? 1 : 0)];
            }
        }
        for(int i = 0; i < N; ++i){
            crc = (crc << 1) ^ tableNew[(Bits.testBit(crc,N-1) ? 1 : 0)];
        }
        return Bits.extractUInt(crc,0,N);
    }

    /**
     * Méthode représentant l'algorithme le plus optimisé pour calculer le CRC24
     * @param table comportant les octets avec lesquels on calcule le CRC24
     * @return le CRC24 sous forme d'un entier.
     */
    private int crc_bitwiseOPTIMAX(byte[] table){
        int[] tableNew = geneTable;
        int crc = 0;

        for(byte b : table){
            crc = ((crc << Byte.SIZE) | Byte.toUnsignedInt(b)) ^ tableNew[Bits.extractUInt(crc,N-Byte.SIZE,Byte.SIZE)];
        }

        for(int i = 0; i<3;++i){
            crc = ((crc << Byte.SIZE) | ((byte)0)) ^ tableNew[Bits.extractUInt(crc,N-Byte.SIZE,Byte.SIZE)];
        }
        return Bits.extractUInt(crc,0,N);
    }

    /**
     * Méthode privée qui permet de générer un tableau, qui va servir
     * à calculer le CRC24 avec l'algorithme le plus optimisé.
     * @param generator générateur pour construire la table.
     * @return le tableau qui permet de retrouver le CRC24
     */
    private static int[] buildTable(int generator) {
        int[] newTable = new int[TABLE_LENGTH];
        for (int i = 0; i < TABLE_LENGTH; ++i) {
            byte[] a = {(byte)i};
            newTable[i]= crc_bitwise(generator,a);
        }
        return newTable;
    }
}
