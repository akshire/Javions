package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URLDecoder;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */


public class AircraftDatabaseTest {

    private final String d = URLDecoder.decode(getClass().getResource("/aircraft.zip").getFile(), UTF_8);
    private final AircraftDatabase  a = new AircraftDatabase(d);

    @Test
    void returnGoodAirCraftData(){
        try{
            AircraftData b = new AircraftData(new AircraftRegistration("HB-JDC"),
                    new AircraftTypeDesignator("A20N"),
                    "AIRBUS A-320neo",
                    new AircraftDescription("L2J"),
                    WakeTurbulenceCategory.MEDIUM);
            assertEquals(b,a.get(new IcaoAddress("4B1814")));
        }catch (IOException e){
            assertEquals(1,0);
        }

    }

    @Test
    void returnGoodAirCraftData2(){
        try{
            AircraftData b = new AircraftData(new AircraftRegistration("RF-76544"),
                    new AircraftTypeDesignator(""),
                    "",
                    new AircraftDescription(""),
                    WakeTurbulenceCategory.UNKNOWN);
            assertEquals(b,a.get(new IcaoAddress("152B00")));
        }catch (IOException e){
            assertEquals(1,0);
        }

    }

    @Test
    void returnGoodAirCraftData3(){
        try{
            AircraftData b = new AircraftData(new AircraftRegistration("N7504U"),
                    new AircraftTypeDesignator("ULAC"),
                    "",
                    new AircraftDescription("L0-"),
                    WakeTurbulenceCategory.UNKNOWN);
            assertEquals(b,a.get(new IcaoAddress("AA1D00")));
        }catch (IOException e){
            assertEquals(1,0);
        }

    }
}
