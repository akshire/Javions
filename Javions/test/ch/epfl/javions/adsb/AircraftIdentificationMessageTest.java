package ch.epfl.javions.adsb;


import ch.epfl.javions.aircraft.IcaoAddress;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class AircraftIdentificationMessageTest {
    protected static final String allMessagesIdentification =
            "AircraftIdentificationMessage[timeStampNs=1499146900, icaoAddress=IcaoAddress[string=4D2228], category=163, callSign=CallSign[string=RYR7JD]]\n" +
            "AircraftIdentificationMessage[timeStampNs=2240535600, icaoAddress=IcaoAddress[string=01024C], category=163, callSign=CallSign[string=MSC3361]]\n" +
            "AircraftIdentificationMessage[timeStampNs=2698727800, icaoAddress=IcaoAddress[string=495299], category=163, callSign=CallSign[string=TAP931]]\n" +
            "AircraftIdentificationMessage[timeStampNs=3215880100, icaoAddress=IcaoAddress[string=A4F239], category=165, callSign=CallSign[string=DAL153]]\n" +
            "AircraftIdentificationMessage[timeStampNs=4103219900, icaoAddress=IcaoAddress[string=4B2964], category=161, callSign=CallSign[string=HBPRO]]\n" +
            "AircraftIdentificationMessage[timeStampNs=4619251500, icaoAddress=IcaoAddress[string=4B1A00], category=162, callSign=CallSign[string=SAZ54]]\n" +
            "AircraftIdentificationMessage[timeStampNs=4750361000, icaoAddress=IcaoAddress[string=4D0221], category=162, callSign=CallSign[string=SVW29VM]]\n" +
            "AircraftIdentificationMessage[timeStampNs=5240059000, icaoAddress=IcaoAddress[string=4CA2BF], category=163, callSign=CallSign[string=RYR5907]]\n" +
            "AircraftIdentificationMessage[timeStampNs=6532434900, icaoAddress=IcaoAddress[string=4D2228], category=163, callSign=CallSign[string=RYR7JD]]\n" +
            "AircraftIdentificationMessage[timeStampNs=7009169300, icaoAddress=IcaoAddress[string=4D029F], category=161, callSign=CallSign[string=JFA17S]]\n" +
            "AircraftIdentificationMessage[timeStampNs=7157583600, icaoAddress=IcaoAddress[string=39CEAA], category=160, callSign=CallSign[string=TVF7307]]\n" +
            "AircraftIdentificationMessage[timeStampNs=7193198000, icaoAddress=IcaoAddress[string=4B17E5], category=163, callSign=CallSign[string=SWR6LH]]\n" +
            "AircraftIdentificationMessage[timeStampNs=7203089700, icaoAddress=IcaoAddress[string=4BCDE9], category=163, callSign=CallSign[string=SXS5GH]]\n" +
            "AircraftIdentificationMessage[timeStampNs=7306755600, icaoAddress=IcaoAddress[string=440237], category=163, callSign=CallSign[string=EJU63HA]]\n";

    /**
     * Test pour les 5 premiers messages du fichier, ce test est plus spécifique
     */
    @Test
    void ofTestFirstFiveMessages() throws IOException {
        try (InputStream stream = new FileInputStream("resources/samples_20230304_1442.bin")) {
            AdsbDemodulator calculator = new AdsbDemodulator(stream);
            int i = 0;
            while (i < 5) {
                RawMessage nextMessage = calculator.nextMessage();

                if (nextMessage.typeCode() > 0 && nextMessage.typeCode()<5) {

                    AircraftIdentificationMessage a = AircraftIdentificationMessage.of(nextMessage);

                    if(i==0) {
                        assertEquals(1499146900L, a.timeStampNs());
                        assertEquals(new IcaoAddress("4D2228"), a.icaoAddress());
                        assertEquals(163, a.category());
                        assertEquals(new CallSign("RYR7JD"), a.callSign());
                    }
                    if(i==1) {
                        assertEquals(2240535600L, a.timeStampNs());
                        assertEquals(new IcaoAddress("01024C"), a.icaoAddress());
                        assertEquals(163, a.category());
                        assertEquals(new CallSign("MSC3361"), a.callSign());
                    }
                    if(i==2) {
                        assertEquals(2698727800L, a.timeStampNs());
                        assertEquals(new IcaoAddress("495299"), a.icaoAddress());
                        assertEquals(163, a.category());
                        assertEquals(new CallSign("TAP931"), a.callSign());
                    }
                    if(i==3) {
                        assertEquals(3215880100L, a.timeStampNs());
                        assertEquals(new IcaoAddress("A4F239"), a.icaoAddress());
                        assertEquals(165, a.category());
                        assertEquals(new CallSign("DAL153"), a.callSign());
                    }
                    if(i==4){
                        assertEquals(4103219900L,a.timeStampNs());
                        assertEquals(new IcaoAddress("4B2964"),a.icaoAddress());
                        assertEquals(161,a.category());
                        assertEquals(new CallSign("HBPRO"),a.callSign());
                    }
                    i++;
                }
            }

        }
    }

    /**
     * Test pour tous les messages du fichier, ce test est un test de bourrin
     * On met tous les messages dans une string et on compare avec la string en attribut privé
     */
    @Test
    void ofTestAllMessages() throws IOException{
        try(InputStream stream = new FileInputStream("resources/samples_20230304_1442.bin")){

            AdsbDemodulator calculator = new AdsbDemodulator(stream);
            RawMessage nextMessage = calculator.nextMessage();
            //String que nous allons construire avec tous les messages dans le fichier
            StringBuilder buildingMessages = new StringBuilder();
            while(nextMessage!=null){
                if(nextMessage.typeCode() <= 4 && nextMessage.typeCode() >= 1){
                    buildingMessages.append(AircraftIdentificationMessage.of(nextMessage).toString()).append("\n");
                }
                nextMessage = calculator.nextMessage();
            }
            assertEquals(allMessagesIdentification, buildingMessages.toString());
        }
    }
}

