package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong
 */
public class AircraftTypeDesignatorTest {
    @Test
    void doesAcceptEmptyString(){
        AircraftTypeDesignator a = new AircraftTypeDesignator("");
    }

    @Test
    void worksWithLegalStatement(){
        AircraftTypeDesignator p = new AircraftTypeDesignator("A20N");
    }

    @Test
    void illegalStatement(){
        assertThrows(IllegalArgumentException.class,()->{
            AircraftTypeDesignator l = new AircraftTypeDesignator("Z");
        });

    }
}
