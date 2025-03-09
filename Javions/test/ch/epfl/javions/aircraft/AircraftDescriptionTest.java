package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Erik Hübner (341205)
 */
public class AircraftDescriptionTest {
    @Test
    void doesAcceptsEmptyString(){
        AircraftDescription a = new AircraftDescription("");
    }
    @Test
    void doesAcceptEmptyString(){
        AircraftDescription o = new AircraftDescription("");
    }

    @Test
    void legalStatement(){
        AircraftDescription l = new AircraftDescription("L2J");
    }

    @Test
    void illegalStatement(){
        assertThrows(IllegalArgumentException.class,()->{
            AircraftDescription k = new AircraftDescription("Z");
        });

    }
}
