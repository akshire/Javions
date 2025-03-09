package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Erik Hübner (341205)
 */
public class IcaoAdressTest {
    @Test
    void doesNotacceptsEmptyString(){
        assertThrows(IllegalArgumentException.class, ()->{
            IcaoAddress a = new IcaoAddress("");
        });
    }

    @Test
    void matchingString(){
        assertThrows(IllegalArgumentException.class,()->{
            String string = "";
            IcaoAddress i = new IcaoAddress(string);
        });


    }

    @Test
    void illegalStatement(){
        assertThrows(IllegalArgumentException.class,()->{
            IcaoAddress m = new IcaoAddress("Z");
        });
    }

    @Test
    void legalStatement(){
        IcaoAddress l = new IcaoAddress("4B1814");
    }
}
