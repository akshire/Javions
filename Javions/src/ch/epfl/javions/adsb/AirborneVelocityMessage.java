package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;
import java.util.Objects;

/**
 * L'enregistrement AirborneVelocityMessage représente un message de vitesse en vol
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 * @param timeStampNs l'horodatage du message, en nanosecondes
 * @param icaoAddress l'adresse OACI de l'expéditeur du message
 * @param speed la vitesse de l'aéronef, en m/s
 * @param trackOrHeading la direction de déplacement de l'aéronef, en radians
 */
public record AirborneVelocityMessage(long timeStampNs,
                                      IcaoAddress icaoAddress,
                                      double speed,
                                      double trackOrHeading) implements Message {

    /**
     * Constructeur compact qui vérifie si l'adresse OACI n'est pas nul,
     * si les attributs timeStampNs, speed, trackOrHeading sont supérieurs ou égaux à 0.
     * @throws NullPointerException si icaoAdress est null
     * @throws IllegalArgumentException si le timeStamp,speed ou trackOrHeading est négatif
     */
    public AirborneVelocityMessage{
        Preconditions.checkArgument(timeStampNs >= 0 && speed >= 0 && trackOrHeading >= 0);
        Objects.requireNonNull(icaoAddress);
    }

    /**
     * Méthode qui renvoi un message de type AirborneVelocityMessage à partir d'un rawMessage
     *
     * @param rawMessage message brut
     * @return un message de vitesse en vol ou null si le sous-type est invalide,
     * ou si la vitesse ou la direction de déplacement ne peuvent pas être déterminés
     */
    public static AirborneVelocityMessage of(RawMessage rawMessage) {

        //extraction des différentes parties du message
        long me = rawMessage.payload();
        int subType = Bits.extractUInt(me, 48, 3);
        double trackOrHeading = 0;
        double speed = 0;

        //validation du sous-type
        if (!(subType == 1 || subType==2 || subType==3 || subType == 4)) {
            return null;
        }
        //cas déplacement par rapport au sol
        if (subType == 1 || subType == 2) {

            double x = Bits.extractUInt(me, 32, 10);
            double y = Bits.extractUInt(me, 21, 10);

            //validation de la vitesse en x et y.
            if (x == 0 || y == 0) {return null;}

            x = Bits.extractUInt(me, 42, 1) == 1 ? -(x - 1) : (x - 1);
            y = Bits.extractUInt(me, 31, 1) == 1 ? -(y - 1) : (y - 1);


            speed = Math.hypot(x, y);
            double aTan = Math.atan2(x,y);

            trackOrHeading = aTan < 0 ? aTan + 2 * Math.PI : aTan;

            if (subType == 1) {


                speed = Units.convertFrom(speed, Units.Speed.KNOT);
            }
            //si le sous-type est égal à 2.
            else {

                speed = Units.convertFrom(speed * 4, Units.Speed.KNOT);
            }

        }
        //cas déplacement dans l'air

        if (subType == 3 || subType == 4) {
            if (!(Bits.testBit(me,42))) {return null;}
            int extraction = Bits.extractUInt(me,21,10);
            if( extraction == 0){
                return null;
            }

            int hdg = Bits.extractUInt(me, 32, 10);

            double hdgTurn = hdg / Math.scalb(1, 10);

            trackOrHeading = Units.convertFrom(hdgTurn, Units.Angle.TURN);

            if (subType == 3) {
                speed = extraction-1;
            }
            //si le sous-type est égal à 4.
            else {
                speed = (extraction -1) * 4;
            }
            speed = Units.convertFrom(speed, Units.Speed.KNOT);
        }
        return new AirborneVelocityMessage(rawMessage.timeStampNs(),
                rawMessage.icaoAddress(),
                speed,
                trackOrHeading);
    }
}
