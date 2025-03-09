package ch.epfl.javions.adsb;


import ch.epfl.javions.GeoPos;

/**
 * La classe AircraftStateAccumulator représente un « accumulateur d'état d'aéronef », c.-à-d.
 * un objet accumulant les messages ADS-B provenant d'un seul aéronef afin de déterminer son état au cours du temps.
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 *
 */
public final class AircraftStateAccumulator<T extends AircraftStateSetter>{
    //private static final double A_SECOND_IN_NANOSECONDS = Math.pow(10,9);
    private static final long TEN_SECOND_IN_NANOSECONDS = 10_000_000_000L;

    private AirbornePositionMessage evenMessage;
    private AirbornePositionMessage oddMessage;
    private final T stateSetter;

    /**
     * Constructeur qui vérifie que l'état modifiable n'est pas nul
     * @param stateSetter état modifiable de l'aéronef
     */
    public AircraftStateAccumulator(T stateSetter){
        if(stateSetter == null){throw new NullPointerException();}
        this.stateSetter = stateSetter;
    }


    /**
     *
     * @return  l'état modifiable de l'aéronef passé à son constructeur
     */
    public T stateSetter(){
        return stateSetter;
    }


    /**
     * Met à jour l'état modifiable en fonction du message donné
     * @param message message donné
     * @throws Exception message pas un des messages défini
     *
     */
    public void update(Message message)throws Exception{
        switch (message){

            case AircraftIdentificationMessage aim -> {
                stateSetter.setLastMessageTimeStampNs(message.timeStampNs());
                stateSetter.setCategory(aim.category());
                stateSetter.setCallSign(aim.callSign());
            }

            case AirbornePositionMessage aim -> {

                if (aim.parity()==1){
                    oddMessage = aim;
                } else{
                    evenMessage = aim;
                }

                stateSetter.setLastMessageTimeStampNs(message.timeStampNs());
                stateSetter.setAltitude(aim.altitude());

                if(evenMessage != null && oddMessage!=null) {
                    //check de l'espacement des deux messages
                    if (timeStampsDifferenceCheck(evenMessage.timeStampNs(), oddMessage.timeStampNs())){
                        GeoPos newPosition = CprDecoder.decodePosition(
                                evenMessage.x(),
                                evenMessage.y(),
                                oddMessage.x(),
                                oddMessage.y(),
                                aim.parity());
                        if(newPosition != null){
                            stateSetter.setPosition(newPosition);
                        }
                    }
                }
            }

            case AirborneVelocityMessage aim -> {
                stateSetter.setLastMessageTimeStampNs(message.timeStampNs());
                stateSetter.setVelocity(aim.speed());
                stateSetter.setTrackOrHeading(aim.trackOrHeading());
            }

            default -> throw new Exception();
        }
    }

    /**
     *
     * @param timeStampNs1 horodatage du message reçu.
     * @param timeStampNs2 l'horodatage du message sauvegardé
     * @return {@code true} si les deux messages sont espacés d'au maximum 10 secondes
     */
    private boolean timeStampsDifferenceCheck(long timeStampNs1, long timeStampNs2){
        long value = Math.abs(timeStampNs1 - timeStampNs2);
        return (value <= TEN_SECOND_IN_NANOSECONDS);
    }


}
