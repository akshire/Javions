package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

/**
 * L'enregistrement AircraftIdentificationMessage représente un message
 * ADS-B d'identification et de catégorie
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 * @param timeStampNs l'horodatage du message, en nanosecondes,
 * @param icaoAddress l'adresse OACI de l'expéditeur du message,
 * @param category la catégorie d'aéronef de l'expéditeur,
 * @param callSign  l'indicatif de l'expéditeur.
 */
public record AircraftIdentificationMessage(long timeStampNs,
                                            IcaoAddress icaoAddress,
                                            int category,
                                            CallSign callSign) implements Message {

    /**
     * Constructeur compact qui vérifie si si icaoAddress ou callSign ne sont pas nuls,
     * puis si timeStampNs est supérieur ou égal à 0.
     * @throws NullPointerException si icaoAdress ou callSign est null
     * @throws IllegalArgumentException si le timeStampNs est négatif
     */
    public AircraftIdentificationMessage{
        Preconditions.checkArgument(timeStampNs >= 0);
        Objects.requireNonNull(icaoAddress);
        Objects.requireNonNull(callSign);


    }

    /**
     *
     * @param rawMessage message brut
     * @return le message d'identification correspondant au message brut donné,
     * ou {@code null} si au moins un des caractères de l'indicatif qu'il contient est invalide
     */
    public static AircraftIdentificationMessage of(RawMessage rawMessage){
        long me = rawMessage.payload();
        int category = Byte.toUnsignedInt((byte)(Bits.extractUInt(me,48,3)|
                ((14-rawMessage.typeCode())<<4)));

        StringBuilder callSign = new StringBuilder();

        //extraction des characters.
        for(int i = 42; i >= 0; i -= 6){

            int character = Bits.extractUInt(me,i,6);

            if(character >= 48 && character <= 57){
                callSign.append((char)character);
            }

            //cas où c'est une lettre majuscule, on ajoute 64, car les lettres majuscules occupent les places 65-91
            else if (character >= 1 && character <= 26) {
                callSign.append((char) (character + 64));
            }

            else if (character == 32) {callSign.append(' ');}

            else{return null;}

        }
        //on enlève les espaces à la fin
        String callSignStr = callSign.toString().stripTrailing();

        return new AircraftIdentificationMessage(
                rawMessage.timeStampNs(),
                rawMessage.icaoAddress(),
                category,
                new CallSign(callSignStr));
    }
}