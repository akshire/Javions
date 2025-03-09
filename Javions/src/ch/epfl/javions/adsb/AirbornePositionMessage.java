package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;


/**
 * L'enregistrement AirbornePositionMessage représente un message ADS-B de positionnement en vol
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 * @param timeStampNs l'horodatage du message, en nanosecondes
 * @param icaoAddress l'adresse OACI de l'expéditeur du message
 * @param altitude l'altitude à laquelle se trouvait l'aéronef au moment de l'envoi du message, en mètres
 * @param parity la parité du message (0 s'il est pair, 1 s'il est impair)
 * @param x la longitude locale et normalisée donc comprise entre 0 et 1 à laquelle se trouvait l'aéronef au moment de l'envoi du message
 * @param y la latitude locale et normalisée à laquelle se trouvait l'aéronef au moment de l'envoi du message
 */
public record AirbornePositionMessage(long timeStampNs,
                                      IcaoAddress icaoAddress,
                                      double altitude,
                                      int parity,
                                      double x,
                                      double y) implements Message{

    /**
     * Constructeur compact qui vérifie si l'adresse OACI n'est pas nul,
     * si le timeStamp est supérieur ou égal à 0, si la parité est 0 ou 1 et finalement si
     * x et y sont compris entre 0 et 1.
     * @throws IllegalArgumentException si la timeStamps est négative ou si la parité, x et y ne respectent pas
     *                                  leurs bornes
     * @throws NullPointerException si icaoAddress est null
     */
    public AirbornePositionMessage{
        Objects.requireNonNull(icaoAddress);
        Preconditions.checkArgument(
                timeStampNs >= 0 &&
                (parity == 0 || parity == 1) &&
                (x >= 0 && x < 1) &&
                (y >= 0 && y < 1));
    }

    /**
     *Méthode qui retourne le message de positionnement en vol correspondant au message brut donné,
     * @param rawMessage message brut
     * @return le message de positionnement en vol ou {@code null} si l'altitude qu'il contient est invalide
     */
    public static AirbornePositionMessage of(RawMessage rawMessage){
        //extraction des différentes parties du message
        long me = rawMessage.payload();
        int parity = Bits.extractUInt(me,34,1);
        double x = Math.scalb(Bits.extractUInt(me,0,17),(-17));
        double y = Math.scalb(Bits.extractUInt(me,17,17),(-17));
        double altitude;


        if(Bits.testBit(me,40)){
            altitude = 25 *
                    ((Bits.extractUInt(me,41,7) << 4) |
                    Bits.extractUInt(me,36,4)) - 1000;
        }
        else{

            long shuffled = 0;
            //extraction des différentes parties
            for(int i = 0; i < 2 ; i += 1) {
                int vectorBits1 =
                        (Bits.extractUInt(me,40 + i * 6,1) << 2) |
                        (Bits.extractUInt(me,38 + i * 6,1) << 1) |
                        (Bits.extractUInt(me,36 + i * 6,1));

                int vectorBits2 =
                        (Bits.extractUInt(me,41 + i * 6,1) << 2) |
                        (Bits.extractUInt(me,39 + i * 6,1) << 1) |
                        (Bits.extractUInt(me,37 + i * 6,1));


                shuffled = (shuffled << (i * 3)) | ((long) vectorBits1 << 6) | vectorBits2;
            }


            int gray9 = Bits.extractUInt(shuffled,3,9);
            int gray3 = Bits.extractUInt(shuffled,0,3);

            //on applique l'algorithme de Gray
            int gray9ToBinary = grayAlgorithm(9,gray9);
            int gray3toBinary = grayAlgorithm(3,gray3);


            if(gray3toBinary == 0 || gray3toBinary == 5 || gray3toBinary == 6){return null;}

            if (gray3toBinary == 7) {gray3toBinary = 5;}

            if((gray9ToBinary % 2) == 1){gray3toBinary = 6 - gray3toBinary;}

            altitude = (gray3toBinary * 100) + (gray9ToBinary * 500) - 1300;
        }
        return new AirbornePositionMessage(rawMessage.timeStampNs(),rawMessage.icaoAddress(),
                Units.convertFrom(altitude,Units.Length.FOOT) ,parity,x,y);
    }

    /**
     * Méthode privée servant dans la méthode {@code of} de {@code AirbornePositionMessage}
     * @param longueur du code de Gray
     * @param grayI code de Gray
     * @return résultat de l'algorithme de Gray
     */
    private static int grayAlgorithm(int longueur, int grayI){
        int decodedGray = 0;
        for(int i = 0; i < longueur; i += 1){decodedGray ^= (grayI >> i);}
        return decodedGray;
    }
}
