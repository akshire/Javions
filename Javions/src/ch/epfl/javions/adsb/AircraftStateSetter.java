package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

/**
 * L'interface AircraftStateSetter a pour but d'être implémentée par toutes
 * les classes représentant l'état (modifiable) d'un aéronef.
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public interface AircraftStateSetter {

    /**
     * Change l'horodatage du dernier message reçu de l'aéronef à la valeur donnée.
     * @param timeStampNs time stamps
     */
    void setLastMessageTimeStampNs(long timeStampNs);

    /**
     * Change la catégorie de l'aéronef à la valeur donnée
     * (le concept de catégorie d'un aéronef sera décrit dans une étape ultérieure).
     * @param category catégorie
     */
    void setCategory(int category);

    /**
     * Change l'indicatif de l'aéronef à la valeur donnée.
     * @param callSign indicatif
     */
    void setCallSign(CallSign callSign);

    /**
     * Change la position de l'aéronef à la valeur donnée.
     * @param position position
     */
    void setPosition(GeoPos position);


    /**
     * Change l'altitude de l'aéronef à la valeur donnée.
     * @param altitude altitude
     */
    void setAltitude(double altitude);

    /**
     * Change la vitesse de l'aéronef à la valeur donnée.
     * @param velocity vitesse
     */
    void setVelocity(double velocity);

    /**
     * Change la direction de l'aéronef à la valeur donnée.
     * @param trackOrHeading direction
     */
    void setTrackOrHeading(double trackOrHeading);
}
