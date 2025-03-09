package ch.epfl.javions.gui;


import ch.epfl.javions.ByteString;
import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.*;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;

import static java.lang.Math.PI;
import static java.lang.Thread.sleep;

/**
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public class AircraftStateManagerTest {
    final char[] directions = new char[]{'↑', '↗', '→', '↘', '↓', '↙', '←', '↖'};

    private char trajectory(double trackOrHeading) {
        if (trackOrHeading >= 2 * PI - PI / 8 || trackOrHeading < PI / 8) {
            return directions[0];
        }
        if (PI / 8 <= trackOrHeading && trackOrHeading <= PI / 8 + PI / 4) {
            return directions[1];
        }
        if (PI / 8 + PI / 4 <= trackOrHeading && trackOrHeading < PI / 8 + PI / 2) {
            return directions[2];
        }
        if (PI / 8 + PI / 2 <= trackOrHeading && trackOrHeading <  PI / 2 + PI / 4 + PI / 8) {
            return directions[3];
        }
        if (PI / 2 + PI / 4 + PI / 8 <= trackOrHeading && trackOrHeading < PI + PI / 8 ) {
            return directions[4];
        }
        if (PI + PI / 8 <=trackOrHeading && trackOrHeading < PI + PI / 4 + PI / 8) {
            return directions[5];
        }
        if (PI + PI / 4 + PI / 8<= trackOrHeading && trackOrHeading < PI + PI / 2 + PI / 8) {
            return directions[6];
        }
        if (PI + PI / 2 + PI / 8 <= trackOrHeading && trackOrHeading < 2 * PI - PI / 8) {
            return directions[7];
        }
        return 0;
    }



    @Test
    void profExample() throws Exception {
        
        Set<ObservableAircraftState> set = null;
        ObservableList<ObservableAircraftState> list = FXCollections.observableArrayList();
        AircraftStateManager mana = null;
        try (DataInputStream s = new DataInputStream(new BufferedInputStream(new FileInputStream("resources/messages_20230318_0915.bin")))) {
            byte[] bytes = new byte[RawMessage.LENGTH];
            int i = 0;
            mana = new AircraftStateManager(new AircraftDatabase("resources/aircraft.zip"));



            while (true) {

                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                ByteString message = new ByteString(bytes);
                RawMessage raw = new RawMessage(timeStampNs, message);
                Message message1 = MessageParser.parse(raw);
                if(message1!=null && message1.icaoAddress().string().compareTo("4D2442")==0){
                    if(message1 instanceof AircraftIdentificationMessage){
                        //System.out.println(((AircraftIdentificationMessage) message1).callSign());
                    }
                }


                mana.updateWithMessage(message1);
                for (ObservableAircraftState o :
                        mana.states()) {
                    System.out.println(o.observableTrajectory());

                }
                mana.purge();





            }
        } catch (EOFException e) {
            System.out.printf("%9s%11s%19s%25s%20s%14s%10s%10s","OACI ","INDICATIF ","IMMATRICULATION ","MODELE ","LONGITUDE ","LATITUDE ","ALTITUDE ","VITESSE ");
            System.out.println();
            System.out.println("_________________________________________________________________________________________________________________________________________________________________");
            set = mana.states();
            for(ObservableAircraftState o : set){
                list.add(o);
            }

            FXCollections.sort(list,new AddressComparator());
            for(ObservableAircraftState o : list){

                System.out.printf("%9s%9s%15s%35s%15f%15f%8d%9s%2s",o.address().string(),
                        ((o.getCallSign() != null) ? o.getCallSign().string() :""),
                        ((o.getData()!=null && o.getData().registration()!=null) ? o.getData().registration().string(): ""),
                        ((o.getData()!=null && o.getData().model()!=null)? o.getData().model():""),
                        Units.convert(o.getPosition().longitude(),Units.Angle.RADIAN,Units.Angle.DEGREE),
                        Units.convert(o.getPosition().latitude(),Units.Angle.RADIAN,Units.Angle.DEGREE),
                        (int)o.getAltitude(),
                        (int)Math.rint(Units.convert(o.getVelocity(),Units.Speed.METER_PER_SECOND,Units.Speed.KILOMETER_PER_HOUR)),
                        trajectory(o.getTrackOrHeading()));
                System.out.printf("\n");
            }
        }
    }

    private static class AddressComparator
            implements Comparator<ObservableAircraftState> {
        public AddressComparator(){}
        public int compare(ObservableAircraftState o1,
                           ObservableAircraftState o2) {
            String s1 = o1.address().string();
            String s2 = o2.address().string();
            return s1.compareTo(s2);
        }
    }
}
