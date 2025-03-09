package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Erik Hübner (341205)
 */
public class AircraftRegistrationTest {
    @Test
    void doesNotacceptEmptyString(){
        assertThrows(IllegalArgumentException.class, ()->{
            AircraftRegistration a = new AircraftRegistration("");
        });
    }
}
