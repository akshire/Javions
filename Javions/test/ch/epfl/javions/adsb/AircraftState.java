package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

/**
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public class AircraftState implements AircraftStateSetter{


    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {
        System.out.println("setLastMessageTimeStampNs : " + timeStampNs);
    }

    @Override
    public void setCategory(int category) {
        System.out.println("setCategory : " + category);

    }

    @Override
    public void setCallSign(CallSign callSign) {
        System.out.println("indicatif : " + callSign);

    }

    @Override
    public void setPosition(GeoPos position) {
        System.out.println("position : " + position);
    }

    @Override
    public void setAltitude(double altitude) {
        System.out.println("altitude : " + altitude);
    }

    @Override
    public void setVelocity(double velocity) {
        System.out.println("setVelocity : " + velocity);
    }

    @Override
    public void setTrackOrHeading(double trackOrHeading) {
        System.out.println("setTrackOrHeading : " + trackOrHeading);
    }
}
