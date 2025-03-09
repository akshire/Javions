package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

/**
 * La classe PowerComputer représente un « calculateur de puissance », c.-à-d. un objet
 * capable de calculer les échantillons de puissance du signal à partir des échantillons signés
 * produits par un décodeur d'échantillons.
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public final class PowerComputer {
    private final SamplesDecoder samplesDecoder;
    private final int batchSize;
    private final short[] tableau;
    private int SampleIndex;
    //tableau des rotations
    private final short[] tableauDEchantillon;

    /**
     * Constructeur
     *
     * @param stream flot entrant
     * @param batchSize taille d'un lot
     * @throws IllegalArgumentException si la bachSize n'est pas un multiple de 8
     */

    public PowerComputer(InputStream stream, int batchSize) {
        Preconditions.checkArgument((batchSize % 8) == 0);

        // 2 * batchSize car on ajoute 2 échantillons à chaque pas
        samplesDecoder = new SamplesDecoder(stream, batchSize * Short.BYTES);
        tableau = new short[batchSize * Short.BYTES];
        this.batchSize = batchSize;
        SampleIndex = 0;
        tableauDEchantillon = new short[8];
    }

    /**
     *
     * @param batch tableau dans lequel on enregistre les échantillons de puissance.
     * @return nombre d'échantillons calculés.
     * @throws IOException s'il y a un problème avec le flot
     */
    public int readBatch(int[] batch) throws IOException {
        Preconditions.checkArgument(batch.length == batchSize);

        int decodedSamples = samplesDecoder.readBatch(tableau);
        int count = 0;
        //pour tous les échantillons décodés
        for (int i = 0; i < decodedSamples; i += Short.BYTES) {

            //début de la collecte d'échantillons
            if(i <= 6){
                tableauDEchantillon[i] = tableau[i];
                tableauDEchantillon[i + 1] = tableau[i + 1];
            }
            //lorsque notre tableau d'échantillons est plein
            else{

                tableauDEchantillon[SampleIndex] = tableau[i];
                tableauDEchantillon[SampleIndex+1] = tableau[i + 1];
                SampleIndex += 2;
                SampleIndex = SampleIndex % 8;
            }

            int I =  tableauDEchantillon[SampleIndex]
                    -tableauDEchantillon[(SampleIndex + 2) % 8]
                    +tableauDEchantillon[(SampleIndex + 4) % 8]
                    -tableauDEchantillon[(SampleIndex + 6) % 8];


            int Q =
                    tableauDEchantillon[(SampleIndex + 1) % 8]
                    -tableauDEchantillon[(SampleIndex + 3) % 8]
                    +tableauDEchantillon[(SampleIndex + 5) % 8]
                    -tableauDEchantillon[(SampleIndex + 7) % 8];

            //on place l'échantillon à sa place donc i/2, car à chaque 2 pas, on crée un échantillon
            batch[i / 2] = (int)(Math.pow(I, 2) + Math.pow(Q, 2));
            count += 1;

        }
        return count;
    }
}
