package ch.epfl.javions.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 * La classe StatusLineController gère la ligne d'état
 *
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public final class StatusLineController {

    private final BorderPane pane;
    private final IntegerProperty aircraftCount;
    private final LongProperty messageCount;

    /**
     * Constructeur par default
     */
    public StatusLineController(){
        this.messageCount = new SimpleLongProperty();
        messageCount.set(0);
        this.aircraftCount = new SimpleIntegerProperty();
        pane = new BorderPane();
        pane.getStyleClass().add("status.css");

        Text textAircraftCount = new Text();
        textAircraftCount.textProperty().bind(Bindings.createStringBinding(()->
                String.format("Aéronefs visibles : %d",aircraftCount.get()),
                aircraftCount));

        Text textMessageCount = new Text();
        textMessageCount.textProperty().bind(Bindings.createStringBinding(()->
                String.format("Messages reçus : %d", messageCount.get()),
                messageCount));

        pane.leftProperty().set(textAircraftCount);
        pane.rightProperty().set(textMessageCount);


    }

    /**
     * Getter du panneau de la ligne d'état
     *
     * @return le panneau correspondant à la ligne d'état
     */
    Pane pane(){ return pane;}

    /**
     * Getter de la propriété (modifiable) contenant le nombre d'aéronefs actuellement visibles
     *
     * @return la propriété (modifiable) contenant le nombre d'aéronefs actuellement visibles
     */
    public IntegerProperty aircraftCountProperty(){
        return aircraftCount;
    }

    /**
     * Getter de la propriété (modifiable) contenant le nombre de messages reçus
     * depuis le début de l'exécution du programme
     *
     * @return la propriété (modifiable) contenant le nombre
     *          de messages reçus depuis le début de l'exécution du programme
     */
    public LongProperty messageCountProperty(){
        return messageCount;
    }
}
