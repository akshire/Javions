package ch.epfl.javions.adsb;

import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public class MessageParserTest {
    @Test
    void parseTest() throws IOException {
        try(InputStream stream = new FileInputStream("resources/samples_20230304_1442.bin")){
            AdsbDemodulator compute = new AdsbDemodulator(stream);
            StringBuilder velocity = new StringBuilder();
            StringBuilder position = new StringBuilder();
            StringBuilder identification = new StringBuilder();
            RawMessage m;
            while ((m = compute.nextMessage()) != null) {
                Message a = MessageParser.parse(m);
                if(a instanceof AircraftIdentificationMessage){identification.append(a).append("\n");}
                if(a instanceof AirborneVelocityMessage){velocity.append(a).append("\n");}
                if(a instanceof AirbornePositionMessage){position.append(a).append("\n");}
            }

            assertEquals(AirbornePositionMessageTest.allMessagesPosition,position.toString());
            assertEquals(AirborneVelocityMessageTest.allMessagesVelocity,velocity.toString());
            assertEquals(AircraftIdentificationMessageTest.allMessagesIdentification,identification.toString());
        }
    }
}
