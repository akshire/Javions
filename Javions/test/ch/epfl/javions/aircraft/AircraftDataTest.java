package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */

public class AircraftDataTest {
    @Test
    void constructorThrowsError(){
        assertThrows(NullPointerException.class,()->{
           AircraftData a = new AircraftData(null,null,null,null,null);
        });
        assertThrows(NullPointerException.class,()->{
            AircraftData a = new AircraftData(new AircraftRegistration("HB-JDC"),null,null,null,null);
        });

    }

    @Test
    void constructorDoesNotThrow(){
        AircraftData a = new AircraftData(new AircraftRegistration("HB-JDC"),new AircraftTypeDesignator("A20N") ,"TEST",new AircraftDescription("L2J"),WakeTurbulenceCategory.MEDIUM);

    }
}
