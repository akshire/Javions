package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * La classe SamplesDecoder représente un décodeur d'échantillons.
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public final class SamplesDecoder {
    private final InputStream stream;
    private final byte[] tempArray;
    private final int batchSize;
    private static final short BIASED = 2048;

    /**
     * Constructeur
     *
     * @param stream le flot dans lequel se trouve les échantillons
     * @param batchSize la taille d'un lot
     * @throws IllegalArgumentException si la bachsize n'est pas plus grande que 0(exclu)
     * @throws NullPointerException si le stream est null
     */
    public SamplesDecoder(InputStream stream, int batchSize){
        Objects.requireNonNull(stream);
        Preconditions.checkArgument(batchSize>0);

        this.stream = stream;
        this.batchSize = batchSize;
        tempArray = new byte[batchSize*2];
    }

    /**
     *
     * @param batch tableau dans lequel on stocke le lot
     * @return nombre d'échantillons décodés.
     * @throws IOException si la méthode relève un problème avec le flot.
     * @throws IllegalArgumentException si la longueur de la bach n'et pas égale à la bachSize
     */
    public int readBatch(short[] batch) throws IOException {
        Preconditions.checkArgument(batch.length==batchSize);

        int count = 0;
        int numberOfBytesRead = stream.readNBytes(tempArray,0,batchSize * 2);
        for(int i = 0 ; i < tempArray.length ; i += 2){
            int sample = 0;
            sample = sample | Byte.toUnsignedInt(tempArray[i+1]);
            sample = (sample << Byte.SIZE ) | Byte.toUnsignedInt(tempArray[i]);

            batch[i / 2]= (short) (sample - BIASED);

            count += 1;
        }
        return numberOfBytesRead != batchSize * 2 ? numberOfBytesRead / 2 : count;
    }
}
