package ch.epfl.javions.demodulation;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.adsb.RawMessage;
import java.io.IOException;
import java.io.InputStream;

/**
 * La classe AdsbDemodulator représente un démodulateur de messages ADS-B.
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public final class AdsbDemodulator {
    private final PowerWindow pWindow;
    private static final int WINDOW_SIZE = 1200;
    private int pSumMinusOne;
    private static final Crc24 COMPUTE = new Crc24(Crc24.GENERATOR);

    /**
     * Constructeur
     *
     * @param samplesStream flot entrant
     * @throws IOException s'il y a un problème avec le flot.
     */
    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        //on vérifie si le flot est nul
        pWindow = new PowerWindow(samplesStream, WINDOW_SIZE);
        pSumMinusOne = pWindow.get(0) + pWindow.get(10) + pWindow.get(35) + pWindow.get(45);
    }

    /**
     *
     * @return Le prochain message ADS-B du flot d'échantillons passé au constructeur, ou {@code null}
     * s'il n'y en a plus, c'est-à-dire que la fin du flot d'échantillons a été atteinte.
     * @throws IOException en cas d'erreur d'entrée/sortie
     */
    public RawMessage nextMessage() throws IOException{

        while(pWindow.isFull()){

            int pSum = pWindow.get(0)
                    + pWindow.get(10)
                    + pWindow.get(35)
                    + pWindow.get(45);

            int vSum = pWindow.get(5)
                    + pWindow.get(15)
                    + pWindow.get(20)
                    + pWindow.get(25)
                    + pWindow.get(30)
                    + pWindow.get(40);

            int pSumPlusOne = pWindow.get(1)
                    + pWindow.get(10+1)
                    + pWindow.get(35+1)
                    + pWindow.get(45+1);

            int previousSample;

            if((pSum >= 2 * vSum) && (pWindow.position() == 0 || (pSumMinusOne < pSum)) && (pSum > pSumPlusOne)){

                int verification = 0;

                //Vérification si le DF correspond au type 17
                for (int k = 0; k < 8; k++) {
                    verification = (verification << 1) | ((pWindow.get(80 + 10 * k) < pWindow.get(85 + 10 * k) ? 0 : 1));
                }

                if((RawMessage.size((byte)verification) == RawMessage.LENGTH)){


                    //Placement du premier élément dans le tableau d'octets.
                    byte[] rawMessage = new byte[RawMessage.LENGTH];
                    rawMessage[0] = (byte)verification;

                    previousSample = pWindow.get(1199);

                    int bitVector = 0;

                    //Décodage du reste des valeurs.
                    for(int j = 8 ; j < 112; j++){
                        bitVector = (bitVector <<1) | ((pWindow.get(80 + 10 * j) < pWindow.get(85 + 10 * j) ? 0 : 1));

                        if(j % 8 == 7){
                            rawMessage[((j+1)/8)-1] = (byte)Byte.toUnsignedInt((byte)bitVector);
                            bitVector = 0;
                        }
                    }
                    long pos = pWindow.position();

                    //Vérification du crc24
                    if(COMPUTE.crc(rawMessage)==0){
                        pWindow.advanceBy(WINDOW_SIZE);
                        pSumMinusOne = previousSample + pWindow.get(9) + pWindow.get(34) + pWindow.get(44);
                        return new RawMessage(pos * 100,new ByteString(rawMessage));
                    }
                }
            }
            previousSample = pWindow.get(0);
            pWindow.advance();
            pSumMinusOne = previousSample + pWindow.get(9) + pWindow.get(34) + pWindow.get(44);
        }
        return null;
    }
}
