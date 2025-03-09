package ch.epfl.javions.gui;


import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import ch.epfl.javions.aircraft.WakeTurbulenceCategory;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import java.util.Objects;
import static javafx.scene.paint.CycleMethod.NO_CYCLE;

/**
 * La classe AircraftController gère la vue des aéronefs
 *
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public final class AircraftController {
    private final MapParameters mapParameters;
    private final Pane pane;
    private final ObjectProperty<ObservableAircraftState> selectedAircraft;

    /**
     * Constructeur
     *
     * @param mapParameters les paramètres de la portion de la carte visible à l'écran
     * @param set l'ensemble (observable, mais non modifiable) des états des aéronefs qui doivent apparaître sur la vue.
     * @param selectedAircraft propriété JavaFX contenant l'état de l'aéronef sélectionné
     */
    public AircraftController(MapParameters mapParameters,ObservableSet<ObservableAircraftState> set,
                               ObjectProperty<ObservableAircraftState> selectedAircraft){
        this.mapParameters = mapParameters;
        this.pane = new Pane();
        this.pane.setPickOnBounds(false);


        set.addListener((SetChangeListener<ObservableAircraftState>)
                change -> {
            if(change.wasAdded()){
                ObservableAircraftState obs = change.getElementAdded();

                SVGPath iconGroup = icon(obs);
                Group group = linkingTrajectoryWithIconAndLabel(
                        trajectoire(obs),linkingStickerAndIcon(iconGroup,sticker(obs),obs),obs);
                group.viewOrderProperty().bind(obs.altitudeProperty().negate());

                pane.getChildren().add(group);
            } else if (change.wasRemoved()) {
                pane.getChildren().removeIf(node -> node.getId().equals(change.getElementRemoved().address().string()));
            }
                });
        this.selectedAircraft = selectedAircraft;



    }


    /**
     * Méthode servant à accéder au panneau contenant les aéronefs et leurs trajectoires
     *
     * @return le pane affichant les aéronefs et leur trajectoire ainsi que l'étiquette
     */
    public Pane pane(){
    return pane;
    }


    private Group linkingTrajectoryWithIconAndLabel(Group groupTrajectory,
                                                    Group groupIconLabel,
                                                    ObservableAircraftState observableAircraftState){
        Group group = new Group(groupTrajectory,groupIconLabel);
        group.viewOrderProperty().bind(observableAircraftState.altitudeProperty());
        group.setId(observableAircraftState.address().string());
        return group;
    }

    /**
     * Méthode servant à lier l'icône avec l'étiquette et donc à créer les liens avec les différentes propriétés
     *
     * @param svgPath noeud JavaFX
     * @param sticker groupe contenant l'icône
     * @param observableAircraftState état observable de l'aéronef
     * @return groupe contenant l'étiquette et l'icône de l'aéronef
     */
    private Group linkingStickerAndIcon(SVGPath svgPath,
                                        Group sticker,
                                        ObservableAircraftState observableAircraftState){

        Group group = new Group(svgPath,sticker);

        group.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> WebMercator.x(mapParameters.getZoom(), observableAircraftState.getPosition().longitude())
                        - mapParameters.getMinX()

                ,observableAircraftState.positionProperty()
                ,mapParameters.minXProperty()
                ,mapParameters.zoomProperty()));
        group.layoutYProperty().bind(Bindings.createDoubleBinding(
                () -> WebMercator.y(mapParameters.getZoom(), observableAircraftState.getPosition().latitude())
                        - mapParameters.getMinY()

                ,observableAircraftState.positionProperty()
                ,mapParameters.minYProperty()
                ,mapParameters.zoomProperty()));


        return group;

    }


    /**
     * Méthode servant à créer l'icône de l'aéronef
     *
     * @param observableAircraftState état observable de l'aéronef
     * @return le nœud contenant l'icône de l'aéronef
     */
    private SVGPath icon(ObservableAircraftState observableAircraftState){


        AircraftData data = observableAircraftState.getData();
        ObjectProperty<AircraftIcon> aircraftIconObjectProperty = new SimpleObjectProperty<>();
        aircraftIconObjectProperty.bind(Bindings.createObjectBinding(()->{
            if(Objects.isNull(data)){
                return  AircraftIcon.iconFor(new AircraftTypeDesignator(""),
                        new AircraftDescription(""),
                        observableAircraftState.getCategory(),
                        WakeTurbulenceCategory.of(""));
            }else{
                return  AircraftIcon.iconFor(data.typeDesignator(),
                        data.description(),
                        observableAircraftState.getCategory(),
                        data.wakeTurbulenceCategory());
            }
        },observableAircraftState.categoryProperty()));



        SVGPath svgPath = new SVGPath();
        svgPath.setStroke(Color.BLACK);
        svgPath.contentProperty().set(aircraftIconObjectProperty.getValue().svgPath());
        svgPath.contentProperty().bind(Bindings.createObjectBinding(() ->
                        aircraftIconObjectProperty.getValue().svgPath(), aircraftIconObjectProperty));

        svgPath.setFill(ColorRamp.PLASMA.at(
                Math.pow((observableAircraftState.getAltitude()/12000),1d/3d)
        ));
        observableAircraftState.altitudeProperty().addListener(e-> svgPath.setFill(ColorRamp.PLASMA.at(
                Math.pow((observableAircraftState.getAltitude()/12000),1d/3d)
        )));



        //re-dessin de l'image avec le bon angle
        svgPath.rotateProperty().bind(Bindings.createDoubleBinding(() -> {
            if(aircraftIconObjectProperty.getValue().canRotate()){
                return Units.convertTo(observableAircraftState.trackOrHeadingProperty().getValue(),Units.Angle.DEGREE);
            }else{
                return 0d;
            }
        }
        ,observableAircraftState.trackOrHeadingProperty()));




        svgPath.getStyleClass().add("aircraft.css");
        svgPath.setOnMouseClicked(mouseEvent -> this.selectedAircraft.set(observableAircraftState));

        return svgPath;
    }

    /**
     * Méthode se chargeant de créer l'étiquette
     *
     * @param observableAircraftState état observable d'un aéronef
     * @return groupe contenant le text et le fond (Rectangle) correspondant de l'aéronef
     */
    private Group sticker(ObservableAircraftState observableAircraftState){
        Text text = new Text();
        Rectangle rectangle = new Rectangle();

        //positionnement du texte + customisation

        text.fillProperty().set(Color.WHITE);
        text.setTabSize(10);

        text.layoutYProperty().set(15);
        text.layoutXProperty().set(4);

        //customization du rectangle

        rectangle.fillProperty().set(Color.GRAY.interpolate(Color.TRANSPARENT,0.70));
        rectangle.setStroke(Color.BLACK);
        rectangle.setArcWidth(10);
        rectangle.setArcHeight(10);

        StringProperty stringProperty = new SimpleStringProperty();

        /*
            lier le Text avec les différents composants
         */

        if(observableAircraftState.getData() == null || observableAircraftState.getData().registration() == null){
            stringProperty.set(observableAircraftState.address().string());
            observableAircraftState.callSignProperty().addListener((o,oV,nV) -> {
                if(nV != null) {
                    stringProperty.set(nV.string());
                }else {
                    stringProperty.set(observableAircraftState.address().string());
                }
            });
        }else{
            stringProperty.set(observableAircraftState.getData().registration().string());

        }

        text.textProperty().bind(
                Bindings.createStringBinding(() -> String.format(stringProperty.getValue() +"\n%s km/h\u2002%s m"
                        ,Double.isNaN(observableAircraftState.getVelocity()) ? "?":
                                String.format("%.0f", observableAircraftState.getVelocity())
                        ,Double.isNaN(observableAircraftState.getAltitude()) ? "?":
                                String.format("%.0f",observableAircraftState.getAltitude()))
                                ,observableAircraftState.velocityProperty()
                                ,observableAircraftState.altitudeProperty()
                                ,stringProperty));

        /*
        binding du rectangle avec le texte
         */
        rectangle.widthProperty().bind(
                text.layoutBoundsProperty().map(bounds -> bounds.getWidth() + 4));

        rectangle.heightProperty().bind(
                text.layoutBoundsProperty().map(bounds -> bounds.getHeight() + 4));



        Group group = new Group(rectangle,text);


        group.visibleProperty().set(mapParameters.zoomProperty().getValue() >= 11);
        group.visibleProperty().bind(Bindings.createBooleanBinding(() -> (mapParameters.getZoom() >= 11 ||
                        !Objects.isNull(selectedAircraft.get()) &&
                                selectedAircraft.get()
                                        .address()
                                        .string()
                                        .equals(observableAircraftState.address().string())),
                mapParameters.zoomProperty(),
                this.selectedAircraft));


        
        return group;
    }

    /**
     * Méthode servant à la création de la trajectoire en tant que groupe
     *
     * @param observableAircraftState état observable de l'aéronef
     * @return groupe contenant la trajectoire
     */
    private Group trajectoire(ObservableAircraftState observableAircraftState){
        Group group = new Group();
        group.setVisible(false);

        selectedAircraft.addListener(event -> {
            if(selectedAircraft.get().address().string().equals(observableAircraftState.address().string())){
                group.setVisible(true);
                trajectoireBuild(observableAircraftState,group);
            }else {
                group.setVisible(false);
                group.getChildren().clear();
            }
        });




        observableAircraftState.observableTrajectory().addListener(
                (ListChangeListener<ObservableAircraftState.AirbornePos>)
                change -> {
                    if(group.isVisible())trajectoireBuild(observableAircraftState,group);
                });


        group.visibleProperty().addListener(event->{
            if(!group.isVisible()) {
                observableAircraftState
                        .observableTrajectory()
                        .removeListener((ListChangeListener<ObservableAircraftState.AirbornePos>) change ->
                                trajectoireBuild(observableAircraftState,group));
                mapParameters
                        .zoomProperty()
                        .removeListener(change-> {
                            if(group.isVisible()) trajectoireBuild(observableAircraftState,group);
                        });
            }else{
                mapParameters
                        .zoomProperty()
                        .addListener(change-> {
                            if(group.isVisible()) trajectoireBuild(observableAircraftState,group);
                        });
            }
        });

        return group;

    }

    /**
     * Méthode servant à créer les lignes de trajectoire au sein du groupe
     *
     * @param group groupe dans lequel on va recréer les lignes
     * @param observableAircraftState état observable d'un aéronef
     */
    private void trajectoireBuild(ObservableAircraftState observableAircraftState,Group group){
        group.getChildren().clear();
        double xStart = 0;
        double yStart = 0;
        double xEnd;
        double yEnd;
        double previousAltitude = 0;
        boolean firstPos = true;
        for (ObservableAircraftState.AirbornePos airbornePos : observableAircraftState.observableTrajectory()) {
            if(firstPos){
                /*
                On calcule la position de départ de la première ligne sur le canvas
                Donc Position en webMercator
                 */

                xStart = WebMercator.x(mapParameters.getZoom(),
                        airbornePos.position().longitude());

                yStart = WebMercator.y(mapParameters.getZoom(),
                       airbornePos.position().latitude());
                previousAltitude = airbornePos.altitude();
                firstPos = false;

            }else{


                xEnd = WebMercator.x(mapParameters.getZoom(),
                        airbornePos.position().longitude());

                yEnd = WebMercator.y(mapParameters.getZoom(),
                        airbornePos.position().latitude());

                Line newLine = new Line(xStart,yStart,xEnd,yEnd);


                newLine.setStrokeWidth(4);

                if(previousAltitude == airbornePos.altitude()){
                    newLine.setStroke(ColorRamp.PLASMA.at(Math.pow(previousAltitude/12000,1d/3d)));
                } else {
                    Stop stop1 = new Stop(0,ColorRamp.PLASMA.at(Math.pow(previousAltitude/12000,1d/3d)));
                    Stop stop2 = new Stop(1,ColorRamp.PLASMA.at(Math.pow(airbornePos.altitude()/12000,1d/3d)));
                    newLine.setStroke(new LinearGradient(0, 0, 1, 0, true, NO_CYCLE, stop1, stop2));
                }

                group.getChildren().add(newLine);
                xStart = xEnd;
                yStart = yEnd;
                previousAltitude = airbornePos.altitude();
            }
        }
        group.layoutXProperty().bind(mapParameters.minXProperty().negate());
        group.layoutYProperty().bind(mapParameters.minYProperty().negate());
    }
}