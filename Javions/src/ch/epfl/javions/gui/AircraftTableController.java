package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.CallSign;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.*;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * La classe AircraftTableController gère la table des aéronefs affichant les différentes données les concernant.
 *
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public final class AircraftTableController {
    private static final NumberFormat NUMBER_FORMAT_LONG_LAT = numberFormatWithMaxAndMin(4,4);
    private static final NumberFormat NUMBER_FORMAT_VELOCITY_ALTITUDE = numberFormatWithMaxAndMin(0,0);
    private final TableView<ObservableAircraftState> tableView;
    private Consumer<ObservableAircraftState> consumer;


    /**
     * Constructeur
     *
     * @param observableAircraftStateObservableSet l'ensemble (observable, mais non modifiable) des états des aéronefs
     * @param selectedAircraft une propriété JavaFX contenant l'état de l'aéronef sélectionné
     */
    public AircraftTableController(ObservableSet<ObservableAircraftState> observableAircraftStateObservableSet,
                                   ObjectProperty<ObservableAircraftState> selectedAircraft) {
        tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        tableView.setTableMenuButtonVisible(true);

        selectedAircraft.addListener((o,oV,nV) -> {
            ObservableAircraftState witness = tableView.getSelectionModel().getSelectedItem();
            if (Objects.isNull(witness) ||  !witness.equals(nV)){
                tableView.getSelectionModel().select(nV);
                tableView.scrollTo(nV);
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((o, oV, nV) -> selectedAircraft.set(nV));

        tableView.setOnMouseClicked(mouseEvent -> {
            if(!Objects.isNull(consumer) && selectedAircraft.getValue() != null){
                if(mouseEvent.getClickCount() == 2 && mouseEvent.getButton() == MouseButton.PRIMARY){
                    consumer.accept(selectedAircraft.getValue());
                }
            }

        });
        tableView.getColumns().add(buildStringColumn(o->o.address().string(),"OACI",60));

        TableColumn<ObservableAircraftState,String> callSignColumn = new TableColumn<>("Indicatif");
        callSignColumn.setPrefWidth(70);
        callSignColumn.setCellValueFactory(observableAircraftState ->
                observableAircraftState.getValue().callSignProperty().map(CallSign::string));
        tableView.getColumns().add(callSignColumn);


        tableView.getColumns().add(buildStringColumn(o -> Objects.isNull(o.getData()) ?
                         null:
                        o.getData().registration().string(),
                "Immatriculation",
                90));
        tableView.getColumns().add(buildStringColumn(o -> Objects.isNull(o.getData()) ?
                        null:
                        o.getData().model(),
                "Model",
                230));
        tableView.getColumns().add(buildStringColumn(o -> Objects.isNull(o.getData()) ?
                        null:
                        o.getData().typeDesignator().string(),
                "Type",
                50));
        tableView.getColumns().add(buildStringColumn(o -> Objects.isNull(o.getData()) ?
                        null:
                        o.getData().description().string(),
                "Description",
                70));

        tableView.getColumns().add(buildNumericColumn(o ->
                        DoubleExpression.doubleExpression(
                                o.positionProperty().map(pos ->
                                        Units.convertTo(pos.longitude(),
                                                Units.Angle.DEGREE))),
                "Longitude (°)",
                NUMBER_FORMAT_LONG_LAT));

        tableView.getColumns().add(buildNumericColumn(o ->
                        DoubleExpression.doubleExpression(
                                o.positionProperty().map(pos ->
                                        Units.convertTo(pos.latitude(),
                                                Units.Angle.DEGREE))),
                "Latitude (°)",
                NUMBER_FORMAT_LONG_LAT));

        tableView.getColumns().add(
                buildNumericColumn(ObservableAircraftState::altitudeProperty,
                        "Altitude (m)",
                        NUMBER_FORMAT_VELOCITY_ALTITUDE));

        tableView.getColumns().add(
                buildNumericColumn(ObservableAircraftState::velocityProperty,
                "Vitesse (km/h)",
                NUMBER_FORMAT_VELOCITY_ALTITUDE));


        tableView.getStylesheets().add("table.css");

        observableAircraftStateObservableSet.addListener((SetChangeListener<ObservableAircraftState>)
                change -> {
                    if(change.wasAdded()){
                        tableView.getItems().add(change.getElementAdded());
                        tableView.sort();
                    } else if (change.wasRemoved()) {
                        tableView.getItems().remove(change.getElementRemoved());
                    }
                });
    }


    /**
     * Méthode retournant la table des aéronefs.
     *
     * @return la table comportant les données relatives aux différents aéronefs.
     */
    public TableView<ObservableAircraftState> pane(){
        return tableView;
    }

    /**
     * Méthode stockant le consommateur passé en argument dans un attribut de la classe.
     *
     * @param consumer le consommateur nécessitant être stocké dans un attribut de la classe,
     *                  comportant l'action à effectuer lors d'un double click ultérieur
     */

    public void setOnDoubleClick(Consumer<ObservableAircraftState> consumer){
        this.consumer = consumer;
    }

    /**
     * Méthode servant à créer une colonne textuelle.
     *
     * @param function expression que la colonne contient
     * @param name nom de la colonne
     * @param prefWidth largeur préférée de la colonne
     * @return la colonne correspondant au nom donné
     */

    private static TableColumn<ObservableAircraftState,String> buildStringColumn(
            Function<ObservableAircraftState, String> function,
            String name,
            double prefWidth
            ){
        TableColumn<ObservableAircraftState,String> stringTableColumn = new TableColumn<>(name);
        stringTableColumn.setPrefWidth(prefWidth);
        stringTableColumn.setCellValueFactory(observableAircraftState ->
                new ReadOnlyObjectWrapper<>(function.apply(observableAircraftState.getValue())));
        return stringTableColumn;

    }

    private static TableColumn<ObservableAircraftState,String> buildNumericColumn(
            Function<ObservableAircraftState, DoubleExpression> function,
            String name,
            NumberFormat numberFormat){
        TableColumn<ObservableAircraftState,String> numericColumn = new TableColumn<>(name);
        numericColumn.setPrefWidth(85);
        numericColumn.getStyleClass().add("numeric");
        numericColumn.setCellValueFactory(o -> function.apply(o.getValue()).map(numberFormat::format));


        numericColumn.setComparator((s1,s2) -> {
            if (s1.equals("") || s2.equals("")) {
                return s1.compareTo(s2);
            }else{
                try {
                    return Double.compare(
                            numberFormat.parse(s1).doubleValue(),
                            numberFormat.parse(s2).doubleValue());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return numericColumn;

    }


    /**
     * Méthode créant un NumberFormat
     *
     * @param max max Digits
     * @param min min Digits
     * @return NumberFormat
     */
    private static NumberFormat numberFormatWithMaxAndMin(int max,int min){
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(max);
        numberFormat.setMinimumFractionDigits(min);
        return numberFormat;
    }

}
