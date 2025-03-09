package ch.epfl.javions.adsb;

/**
 * La classe MessageParser a pour but de transformer les messages ADS-B bruts en messages d'un des trois types :
 * identification, position en vol, vitesse en vol.
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public final class MessageParser {
    private MessageParser(){}

    /**
     *
     * @param rawMessage message brut
     * @return  l'instance de AircraftIdentificationMessage,
     * de AirbornePositionMessage ou de AirborneVelocityMessage correspondant au message brut donné,
     * ou null si le code de type de ce dernier ne correspond à aucun de ces trois types de messages,
     * ou s'il est invalide.
     */
    public static Message parse(RawMessage rawMessage){
        switch(rawMessage.typeCode()){
            case 19 -> {return AirborneVelocityMessage.of(rawMessage);}
            case 1,2,3,4 -> {return AircraftIdentificationMessage.of(rawMessage);}
            case 9,10,11,12,13,14,15,16,17,18,20,21,22 -> {return AirbornePositionMessage.of(rawMessage);}
            default -> {return null;}
        }
    }
}
