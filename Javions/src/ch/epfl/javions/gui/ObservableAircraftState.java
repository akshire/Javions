package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.property.*;
import javafx.collections.ObservableList;


import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.unmodifiableObservableList;

/**
 * La classe ObservableAircraftState représente l'état d'un aéronef. Cet état a la caractéristique
 * d'être observable au sens du patron de conception Observer.
 *
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public final class ObservableAircraftState implements AircraftStateSetter {
    private final IcaoAddress icaoAddress;
    private final AircraftData data;
    private long lastTimeStampNsThatSetPosition;

    /**
     * Constructeur
     *
     * @param icaoAddress adresse OACI de l'aéronef
     * @param data les caractéristiques fixes de l'aéronef
     */
    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData data){
        this.data = data;
        this.icaoAddress = icaoAddress;
        trajectory = observableArrayList();
        trajectoryNotModifiable = unmodifiableObservableList(trajectory);
        lastMessageTimeStampNs = new SimpleLongProperty();
        category = new SimpleIntegerProperty();
        callSign = new SimpleObjectProperty<>();
        position = new SimpleObjectProperty<>();
        altitude = new SimpleDoubleProperty(Double.NaN);
        velocity = new SimpleDoubleProperty(Double.NaN);
        trackOrHeading = new SimpleDoubleProperty();
    }

    /**
     * Getter des caractéristiques fixes de l'aéronef
     *
     * @return les caractéristiques fixes de l'aéronef
     */
    public AircraftData getData() {return data;}

    /**
     * Getter de l'adresse OACI de l'aéronef
     *
     * @return l'adresse OACI de l'aéronef
     */
    public IcaoAddress address(){return icaoAddress;}

    private final LongProperty lastMessageTimeStampNs;

    /**
     * Getter de la propriété du timeStamp du dernier message reçu de l'aéronef
     *
     * @return propriété timeStamp du dernier message reçu en lecture seule
     */
    public ReadOnlyLongProperty lastTimeStampNsProperty(){
        return lastMessageTimeStampNs;
    }

    /**
     * Getter de la valeur du timeStamp du dernier message reçu de l'aéronef
     *
     * @return timeStamp du dernier message reçu
     */
    public long getLastTimeStampNs(){
        return lastMessageTimeStampNs.getValue();
    }

    /**
     * Setter de l'horodatage du dernier message reçu de l'aéronef à la valeur donnée.
     *
     * @param timeStampNs timeStamp
     */
    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {
        lastMessageTimeStampNs.set(timeStampNs);
    }

    /*_________________________________________________________________________________________________________________*/

    private final IntegerProperty category;

    /**
     * Getter de la propriété de la catégorie de l'aéronef
     *
     * @return la catégorie de l'aéronef en lecture seule
     */
    public ReadOnlyIntegerProperty categoryProperty(){
        return category;
    }

    /**
     * Getter de la valeur de la catégorie de l'aéronef
     *
     * @return la catégorie de l'aéronef
     */
    public int getCategory(){
        return category.getValue();
    }

    /**
     * Setter de la catégorie de l'aéronef
     *
     * @param category catégorie
     */
    @Override
    public void setCategory(int category) {
        this.category.set(category);
    }

    /*_________________________________________________________________________________________________________________*/

    private final ObjectProperty<CallSign> callSign;

    /**
     * Getter de la propriété de l'indicatif
     *
     * @return l'indicatif de l'aéronef en lecture seule
     */
    public ReadOnlyObjectProperty<CallSign> callSignProperty(){
        return callSign;
    }

    /**
     * Getter de la valeur de l'indicatif
     *
     * @return l'indicatif de l'aéronef
     */
    public CallSign getCallSign(){
        return callSign.getValue();
    }

    /**
     * Setter de l'indicatif de l'aéronef
     *
     * @param callSign indicatif
     */
    @Override
    public void setCallSign(CallSign callSign) {
        this.callSign.set(callSign);
    }

    /*-----------------------------------------------------------------------------------------------------------------*/

    private final ObjectProperty<GeoPos> position;

    /**
     * Getter de la propriété de la dernière position de l'aéronef
     *
     * @return la dernière position de l'aéronef en lecture seule
     */
    public ReadOnlyObjectProperty<GeoPos> positionProperty(){
        return position;
    }

    /**
     * Getter de la valeur de la dernière position de l'aéronef
     *
     * @return la dernière position de l'aéronef
     */
    public GeoPos getPosition(){
        return position.getValue();
    }

    /**
     * Change la position de l'aéronef et ajoute la paire
     * (position courante, altitude courante) à la trajectoire si l'altitude est connue.
     *
     * @param position position de l'aéronef
     */
    public void setPosition(GeoPos position){
        if (!Double.isNaN(altitude.getValue())){
            trajectory.add(new AirbornePos(position,getAltitude()));
        }
        this.position.set(position);

    }

    /*_________________________________________________________________________________________________________________*/

    private final ObservableList<AirbornePos> trajectory;
    private final ObservableList<AirbornePos> trajectoryNotModifiable;

    /**
     * L'enregistrement AirbornePos représente un échantillon de trajectoire de l'aéronef
     *
     * @param position position de l'aéronef
     * @param altitude altitude de l'aéronef
     */
    public record AirbornePos(GeoPos position, double altitude){}

    /**
     * Getter de la trajectoire sous forme de liste observable, mais non modifiable
     *
     * @return la trajectoire de l'aéronef en lecture seule
     */
    public ObservableList<AirbornePos> observableTrajectory(){
        return trajectoryNotModifiable;
    }

    /*_________________________________________________________________________________________________________________*/

    private final DoubleProperty altitude;

    /**
     * Getter de la propriété de l'altitude de l'aéronef
     *
     * @return l'altitude de l'aéronef en [m] en lecture seule
     */
    public ReadOnlyDoubleProperty altitudeProperty(){
        return altitude;
    }

    /**
     * Getter de la valeur de l'altitude de l'aéronef
     *
     * @return l'altitude de l'aéronef en [m]
     */
    public double getAltitude(){
        return altitude.getValue();
    }

    /**
     * Change l'altitude de l'aéronef à la valeur donnée, si la position est continue
     * ou alors si l'horodatage du message courant est identique à celui du message qui a provoqué le dernier
     * ajout à la trajectoire, alors le dernier élément est remplacé par la paire (position courante,
     * altitude courante) à la trajectoire actuelle.
     *
     * @param altitude altitude [m]
     */
    @Override
    public void setAltitude(double altitude) {

        if(getPosition() != null){
            if(trajectory.size() == 0){
                trajectory.add(new AirbornePos(getPosition(),getAltitude()));
            } else if (lastMessageTimeStampNs.get() == lastTimeStampNsThatSetPosition) {
                lastTimeStampNsThatSetPosition = getLastTimeStampNs();
                trajectory.set(trajectory.size()-1,new AirbornePos(getPosition(),getAltitude()));
            }

        }
        this.altitude.set(altitude);
    }

    /*_________________________________________________________________________________________________________________*/

    private final DoubleProperty velocity;

    /**
     * Getter de la propriété de la vitesse de l'aéronef
     *
     * @return la vitesse de l'aéronef en [km/h] en lecture seule
     */
    ReadOnlyDoubleProperty velocityProperty(){
        return velocity;
    }

    /**
     * Getter de la valeur de la vitesse de l'aéronef
     *
     * @return la vitesse de l'aéronef en [km/h]
     */
    public double getVelocity(){
        return velocity.getValue();
    }

    /**
     * Setter de la vitesse de l'aéronef
     *
     * @param velocity vitesse en [m/s]
     */
    @Override
    public void setVelocity(double velocity) {
        this.velocity.set(Units.convertTo(velocity,Units.Speed.KILOMETER_PER_HOUR));
    }

    /*_________________________________________________________________________________________________________________*/

    private final DoubleProperty trackOrHeading;

    /**
     * Getter de la propriété de la direction de l'aéronef
     *
     * @return la direction de l'aéronef en lecture seule
     */
    public ReadOnlyDoubleProperty trackOrHeadingProperty(){
        return trackOrHeading;
    }

    /**
     * Getter de la valeur de la direction de l'aéronef
     *
     * @return la direction de l'aéronef (en radians)
     */
    public double getTrackOrHeading(){
        return trackOrHeading.getValue();
    }

    /**
     * Setter de la direction de l'aéronef
     *
     * @param trackOrHeading direction
     */
    @Override
    public void setTrackOrHeading(double trackOrHeading) {
        this.trackOrHeading.set(trackOrHeading);
    }
}
