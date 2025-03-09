
package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * L'enregistrement RawMessage représente un message ADS-B « brut », c'est-à-dire
 * dont l'attribut ME n'a pas encore été analysé
 * @param timeStampNs horodatage du message
 * @param bytes contenu du message
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public record RawMessage(long timeStampNs, ByteString bytes) {
    public static final int LENGTH = 14;
    private static final Crc24 COMPUTE = new Crc24(Crc24.GENERATOR);


    /**
     *Constructeur qui vérifie si l'horodatage est (strictement) négatif,
     * ou si la chaîne d'octets ne contient pas LENGTH octets.
     */
    public RawMessage{
        Preconditions.checkArgument((timeStampNs >= 0) && (bytes.size() == LENGTH));
    }

    /**
     * @param timeStampNs l'horodatage du message
     * @param bytes le message
     * @return le message ADS-B brut avec l'horodatage et les octets donnés, ou {@code null} si le CRC24 des octets ne vaut pas 0.
     */
    public static RawMessage of(long timeStampNs,byte[] bytes){
        return(COMPUTE.crc(bytes) == 0) ? new RawMessage(timeStampNs,new ByteString(bytes)) : null;
    }

    /**
     * @param byte0 octet donné
     * @return la taille d'un message dont le premier octet est celui donné, et qui vaut LENGTH si l'attribut
     * DF contenu dans ce premier octet vaut 17, et 0 sinon indiquant que le message n'est pas d'un type connu
     */
    public static int size(byte byte0){
        return ((Byte.toUnsignedInt(byte0) >>> 3) == 17) ? LENGTH:0;
    }

    /**
     *
     * @param payload l'attribut ME
     * @return retourne le code de type de l'attribut ME passé en argument.(5 bits de poids fort (index 51-55))
     */
    public static int typeCode(long payload){return Bits.extractUInt(payload,51,5);}

    /**
     * @return Le format du message, c'est-à-dire l'attribut DF stocké dans son premier octet
     */
    public int downLinkFormat(){return bytes.byteAt(0) >>> 3;}

    /**
     * @return l'adresse OACI de l'expéditeur du message
     */
    public IcaoAddress icaoAddress(){return new IcaoAddress(bytes.toString().substring(2,8));}

    /**
     * @return l'attribut ME du message—sa « charge utile ».
     */
    public long payload(){ return bytes.bytesInRange(4,11);}

    /**
     * @return Le code de type du message, c'est-à-dire les cinq bits de poids le plus fort de son attribut ME.
     */
    public int typeCode(){return Bits.extractUInt(payload(),51,5);}
}