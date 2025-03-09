package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.GeoPos;
import ch.epfl.javions.aircraft.IcaoAddress;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public class AircraftStateAccumulatorTest {
    private final String firstMessages =
            "position : (5.620176717638969°, 45.71530147455633°)\n" +
                    "position : (5.621292097494006°, 45.715926848351955°)\n" +
                    "indicatif : CallSign[string=RYR7JD]\n" +
                    "position : (5.62225341796875°, 45.71644593961537°)\n" +
                    "position : (5.623420681804419°, 45.71704415604472°)\n" +
                    "position : (5.624397089704871°, 45.71759032085538°)\n" +
                    "position : (5.625617997720838°, 45.71820789948106°)\n" +
                    "position : (5.626741759479046°, 45.718826316297054°)\n" +
                    "position : (5.627952609211206°, 45.71946484968066°)\n" +
                    "position : (5.629119873046875°, 45.72007002308965°)\n" +
                    "position : (5.630081193521619°, 45.7205820735544°)\n" +
                    "position : (5.631163045763969°, 45.72120669297874°)\n" +
                    "indicatif : CallSign[string=RYR7JD]\n" +
                    "position : (5.633909627795219°, 45.722671514377°)\n" +
                    "position : (5.634819064289331°, 45.72314249351621°)";

    @Test
    void printTest()throws Exception {
        String f = "resources/samples_20230304_1442.bin";
        IcaoAddress expectedAddress = new IcaoAddress("4D2228");

        try (InputStream s = new FileInputStream(f)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            AircraftStateAccumulator<AircraftState> a = new AircraftStateAccumulator<>(new AircraftState());

            while ((m = d.nextMessage()) != null) {
                if (!m.icaoAddress().equals(expectedAddress)) continue;
                Message pm = MessageParser.parse(m);
                if (pm != null){
                    a.update(pm);
                }
            }
        }
    }

    @Test
    void timeStampsTooShort(){
        String f = "samples_20230304_1442.bin";
        IcaoAddress expectedAddress = new IcaoAddress("4D2228");
        try (InputStream s = new FileInputStream("resources/"+f)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            AircraftStateAccumulator<AircraftState> a =
                    new AircraftStateAccumulator<>(new AircraftState());
            while ((m = d.nextMessage()) != null) {
                if (!m.icaoAddress().equals(expectedAddress)) continue;

                Message pm = MessageParser.parse(m);
                if (pm != null) a.update(pm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void timeStampsTooShortOff(){
        try {
            RawMessage a1 = new RawMessage(100000000001L, ByteString.ofHexadecimalString("8D39203559B225F07550ADBE328F"));
            RawMessage b1 = new RawMessage(1000L, ByteString.ofHexadecimalString("8DAE02C85864A5F5DD4975A1A3F5"));
            RawMessage c0 = new RawMessage(0L, ByteString.ofHexadecimalString("8D49529958B302E6E15FA352306B"));
            RawMessage d0 = new RawMessage(10L, ByteString.ofHexadecimalString("8D4241A9601B32DA4367C4C3965E"));

            AircraftState yup = new AircraftState();
            AircraftStateAccumulator<AircraftState> air = new AircraftStateAccumulator<>(yup);

            //ce n est censé rien print
            air.update(MessageParser.parse(a1));
            air.update(MessageParser.parse(c0));
            //la c est censé print
            air.update(MessageParser.parse(b1));

        } catch (Exception e){
            System.out.println("      AAAAAAAAAA       ");
        }
    }
    @Test
    void aircraftStateAccumulatorConstructorThrowsIfStateSetterIsNull() {
        assertThrows(NullPointerException.class, () -> new AircraftStateAccumulator<>(null));
    }

    @Test
    void aircraftStateSetterStateSetterReturnsStateSetter() {
        for (int i = 0; i < 10; i += 1) {
            var stateSetter = new AircraftState();
            var accumulator = new AircraftStateAccumulator<>(stateSetter);
            assertSame(stateSetter, accumulator.stateSetter());
        }
    }

    @Test
    void aircraftStateAccumulatorUpdateUpdatesCategoryAndCallSign() throws Exception {
        var icao = new IcaoAddress("ABCDEF");
        var stateSetter = new AircraftState();
        var accumulator = new AircraftStateAccumulator<>(stateSetter);
        var expectedLastMessageTimeStampNs = -1L;
        var expectedCategory = -1;
        var expectedCallSign = (CallSign) null;
        for (var i = 0; i < 8; i += 1) {
            assertEquals(expectedLastMessageTimeStampNs, stateSetter.lastMessageTimeStampNs);
            assertEquals(expectedCategory, stateSetter.category);
            assertEquals(expectedCallSign, stateSetter.callSign);

            expectedLastMessageTimeStampNs = 101L * i;
            expectedCategory = 0xA0 | i;
            expectedCallSign = new CallSign("BLA" + Integer.toString(i, 3));
            var message = new AircraftIdentificationMessage(expectedLastMessageTimeStampNs, icao, expectedCategory, expectedCallSign);
            accumulator.update(message);
        }
    }

    @Test
    void aircraftStateAccumulatorUpdateUpdatesVelocityAndTrackOrHeading()throws Exception {
        var icao = new IcaoAddress("ABCDEF");
        var stateSetter = new AircraftState();
        var accumulator = new AircraftStateAccumulator<>(stateSetter);
        var expectedLastMessageTimeStampNs = -1L;
        var expectedVelocity = Double.NaN;
        var expectedTrackOrHeading = Double.NaN;
        for (var i = 0; i < 8; i += 1) {
            assertEquals(expectedLastMessageTimeStampNs, stateSetter.lastMessageTimeStampNs);
            assertEquals(expectedVelocity, stateSetter.velocity);
            assertEquals(expectedTrackOrHeading, stateSetter.trackOrHeading);

            expectedLastMessageTimeStampNs = 103L * i;
            expectedVelocity = 10.0 * i;
            expectedTrackOrHeading = 1.99999999 * Math.PI / (i + 1);
            var message = new AirborneVelocityMessage(expectedLastMessageTimeStampNs, icao, expectedVelocity, expectedTrackOrHeading);
            accumulator.update(message);
        }
    }

    @Test
    void aircraftStateAccumulatorUpdateUpdatesAltitudeButNotPositionWhenParityIsConstant()throws Exception{
        var icao = new IcaoAddress("ABCDEF");
        for (int parity = 0; parity <= 1; parity += 1) {
            var stateSetter = new AircraftState();
            var accumulator = new AircraftStateAccumulator<>(stateSetter);

            var expectedLastMessageTimeStampNs = -1L;
            var expectedAltitude = Double.NaN;
            for (int i = 0; i < 100; i += 1) {
                assertEquals(expectedLastMessageTimeStampNs, stateSetter.lastMessageTimeStampNs);
                assertEquals(expectedAltitude, stateSetter.altitude);
                assertNull(stateSetter.position);

                expectedLastMessageTimeStampNs = 107L * i;
                expectedAltitude = -100d + 20d * i;
                var x = 0.999999 / (i + 1d);
                var y = 1d - x;
                var message = new AirbornePositionMessage(expectedLastMessageTimeStampNs, icao, expectedAltitude, parity, x, y);
                accumulator.update(message);
            }
        }
    }

    @Test
    void aircraftStateAccumulatorUpdateUpdatesAltitudeButNotPositionWhenMessagesTooFarApart() throws Exception{
        var icao = new IcaoAddress("ABCDEF");
        var moreThan10s = 10_000_000_001L;
        var stateSetter = new AircraftState();
        var accumulator = new AircraftStateAccumulator<>(stateSetter);

        var x = 0.5;
        var y = 0.5;
        var parity = 0;
        var expectedLastMessageTimeStampNs = -1L;
        var expectedAltitude = Double.NaN;
        for (int i = 0; i < 100; i += 1) {
            assertEquals(expectedLastMessageTimeStampNs, stateSetter.lastMessageTimeStampNs);
            assertEquals(expectedAltitude, stateSetter.altitude);
            assertNull(stateSetter.position);

            expectedLastMessageTimeStampNs += moreThan10s;
            expectedAltitude = -100d + 23d * i;
            parity = 1 - parity;
            var message = new AirbornePositionMessage(expectedLastMessageTimeStampNs, icao, expectedAltitude, parity, x, y);
            accumulator.update(message);
        }
    }

    double cpr(int v) {
        return Math.scalb((double) v, -17);
    }

    @Test
    void aircraftStateAccumulatorUpdateUsesCorrectMessageToComputePosition() throws Exception{
        var icao = new IcaoAddress("ABCDEF");
        var moreThan10s = 10_000_000_001L;
        var stateSetter = new AircraftState();
        var accumulator = new AircraftStateAccumulator<>(stateSetter);

        var timeStampNs = 109L;
        var altitude = 1000d;
        var x0 = cpr(98152);
        var y0 = cpr(98838);
        var x1 = cpr(95758);
        var y1 = cpr(81899);

        var m1 = new AirbornePositionMessage(timeStampNs, icao, altitude, 0, cpr(12), cpr(13));
        accumulator.update(m1);
        assertNull(stateSetter.position);

        timeStampNs += moreThan10s;
        var m2 = new AirbornePositionMessage(timeStampNs, icao, altitude, 0, x0, y0);
        accumulator.update(m2);
        assertNull(stateSetter.position);

        timeStampNs += 1000L;
        var m3 = new AirbornePositionMessage(timeStampNs, icao, altitude, 1, x1, y1);
        accumulator.update(m3);
        assertNotNull(stateSetter.position);
        var p = stateSetter.position;
        assertEquals(6.57520, Math.toDegrees(p.longitude()), 5e-5);
        assertEquals(46.52444, Math.toDegrees(p.latitude()), 5e-5);
    }

    @Test
    void aircraftStateAccumulatorCorrectlyHandlesLatitudeBandChange() throws Exception{
        record ParityXY(int p, int x, int y) { }

        var xys = new ParityXY[]{
                new ParityXY(0, 98152, 106326),
                new ParityXY(1, 95758, 89262),
                new ParityXY(0, 95758, 106330),
                new ParityXY(1, 93364, 89266)
        };
        var expectedLongitudeDeg = 6.57520;
        var expectedLatitudesDeg = new double[]{
                Double.NaN, 46.8672, 46.8672, 46.8674
        };

        var icao = new IcaoAddress("ABCDEF");
        var stateSetter = new AircraftState();
        var accumulator = new AircraftStateAccumulator<>(stateSetter);

        var timeStampNs = 113L;
        var altitude = 567d;

        for (int i = 0; i < xys.length; i += 1) {

            var m = new AirbornePositionMessage(timeStampNs, icao, altitude, xys[i].p, cpr(xys[i].x), cpr(xys[i].y));
            accumulator.update(m);
            var expectedLatitudeDeg = expectedLatitudesDeg[i];
            if (Double.isNaN(expectedLatitudeDeg)) {
                assertNull(stateSetter.position);
            } else {
                assertEquals(expectedLongitudeDeg, Math.toDegrees(stateSetter.position.longitude()), 1e-4);
                assertEquals(expectedLatitudeDeg, Math.toDegrees(stateSetter.position.latitude()), 1e-4);
            }
            timeStampNs += 1000L;
        }
    }

    private static final class AircraftState implements AircraftStateSetter {
        long lastMessageTimeStampNs = -1L;
        int category = -1;
        CallSign callSign = null;
        GeoPos position = null;
        double altitude = Double.NaN;
        double velocity = Double.NaN;
        double trackOrHeading = Double.NaN;

        @Override
        public void setLastMessageTimeStampNs(long timeStampNs) {
            lastMessageTimeStampNs = timeStampNs;
        }

        @Override
        public void setCategory(int category) {
            this.category = category;
        }

        @Override
        public void setCallSign(CallSign callSign) {
            this.callSign = callSign;
        }

        @Override
        public void setPosition(GeoPos position) {
            this.position = position;
        }

        @Override
        public void setAltitude(double altitude) {
            this.altitude = altitude;
        }

        @Override
        public void setVelocity(double velocity) {
            this.velocity = velocity;
        }

        @Override
        public void setTrackOrHeading(double trackOrHeading) {
            this.trackOrHeading = trackOrHeading;
        }
    }
}
