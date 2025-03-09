package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import java.util.*;


/**
 * La classe AircraftStateManager a pour but de garder à jour les états d'un ensemble d'aéronefs
 * en fonction des messages reçus des aéronefs.
 *
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public final class AircraftStateManager {
    private static final long ONE_MINUTE_TIMESTAMP_NS = 60_000_000_000L;
    private final Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> map;
    private final ObservableSet<ObservableAircraftState> set;
    private final ObservableSet<ObservableAircraftState> setUnmodifiable;
    private final AircraftDatabase aircraftDatabase;

    private long lastMessageTimeStampNs;

    /**
     * Constructeur
     *
     * @param aircraftDatabase base de donnée mictronics des aéronefs
     */
    public AircraftStateManager(AircraftDatabase aircraftDatabase) {
        this.map = new HashMap<>();
        this.set = FXCollections.observableSet();
        this.setUnmodifiable = FXCollections.unmodifiableObservableSet(set);
        this.aircraftDatabase = aircraftDatabase;
        lastMessageTimeStampNs = 0;
    }


    /**
     * Getter de la liste non modifiable des aéronefs dont la position est connue
     *
     * @return l'ensemble des aéronefs qui ont été update moins d'une minute avant le dernier message
     */
    public ObservableSet<ObservableAircraftState> states() {
        return setUnmodifiable;
    }

    /**
     * Met à jour un état d'un aéronef avec le message donné.
     *
     * @param message message reçu.
     * @throws Exception s'il y a un problème avec le message.
     */
    public void updateWithMessage(Message message) throws Exception {
        if (message != null) {
            lastMessageTimeStampNs = message.timeStampNs();
            if (!map.containsKey(message.icaoAddress())) {
                map.put(message.icaoAddress(), new AircraftStateAccumulator<>(new ObservableAircraftState(message.icaoAddress(),
                        aircraftDatabase.get(message.icaoAddress()))));
            }
            map.get(message.icaoAddress()).update(message);
            AircraftStateAccumulator<ObservableAircraftState> stateAccumulator = map.get(message.icaoAddress());
            ObservableAircraftState o = stateAccumulator.stateSetter();
            if(o.getPosition() != null) {
                set.add(o);
            }
        }
    }

    /**
     * Supprime les aéronefs dont aucun message n'a été reçu au plus une minute après le dernier message
     */
    public void purge() {
        Iterator<ObservableAircraftState> itObs = set.iterator();
        while(itObs.hasNext()) {
            ObservableAircraftState obs = itObs.next();
            if (lastMessageTimeStampNs - obs.getLastTimeStampNs() > ONE_MINUTE_TIMESTAMP_NS) {
                map.remove(obs.address());
                itObs.remove();
            }
        }
    }
}

