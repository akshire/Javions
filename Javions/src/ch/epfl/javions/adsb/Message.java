package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * L'interface Message a pour but d'être implémentée par toutes les classes
 * représentant des messages ADS-B « analysés »
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public interface Message {

    /**
     *
     * @return  l'horodatage du message, en nanosecondes
     */
    long timeStampNs();

    /**
     *
     * @return  l'adresse OACI de l'expéditeur du message.
     */
    IcaoAddress icaoAddress();



}
