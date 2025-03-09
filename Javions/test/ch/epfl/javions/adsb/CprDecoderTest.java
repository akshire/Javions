package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
import org.junit.jupiter.api.Test;

import static java.lang.Math.scalb;
import static java.lang.Math.toDegrees;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public class   CprDecoderTest {
    @Test
    void decodePositionMostRecent1() {
        double x0, y0, x1, y1;
        x0 = scalb(111600d, -17);
        y0 = scalb(94445d, -17);
        x1 = scalb(108865d, -17);
        y1 = scalb(77558d, -17);


        GeoPos positionMostRecent1 = CprDecoder.decodePosition(x0, y0, x1, y1, 1);

        //approximation mais test le cas mostRecent == 1
        assertEquals(46.322363, Units.convertTo(positionMostRecent1.latitude(), Units.Angle.DEGREE), Math.pow(10, -6));
        assertEquals(7.475166, Units.convertTo(positionMostRecent1.longitude(), Units.Angle.DEGREE), Math.pow(10, -6));


    }

    @Test
    void decodePositionMostRecent0() {
        double x0, y0, x1, y1;
        x0 = scalb(111600d, -17);
        y0 = scalb(94445d, -17);
        x1 = scalb(108865d, -17);
        y1 = scalb(77558d, -17);

        GeoPos positionMostRecent0 = CprDecoder.decodePosition(x0, y0, x1, y1, 0);

        //Test Exact, test le cas mostRecent == 0
        assertEquals(89192898, positionMostRecent0.longitudeT32());
        assertEquals(552659081, positionMostRecent0.latitudeT32());
        //normalement si juste au-dessus c'est juste alors ici ça devrait passer aussi
        assertEquals(46.323349038138986, Units.convert(positionMostRecent0.latitudeT32(), Units.Angle.T32, Units.Angle.DEGREE));
        assertEquals(7.476062346249819, Units.convert(positionMostRecent0.longitudeT32(), Units.Angle.T32, Units.Angle.DEGREE));
    }

    @Test
    void edTest1() {
        double x0 = 0;
        double y0 = 3;
        double x1 = 0;
        double y1 = 0;

        GeoPos position = CprDecoder.decodePosition(x0, y0, x1, y1, 0);
        assertNull(position);
    }

    @Test
    void edTest2() {
        GeoPos pos = CprDecoder.decodePosition(0.62, 0.42, 0.6200000000000000001, 0.4200000000000000001, 0);
        assertEquals(-2.3186440486460924, Units.convertTo(pos.longitude(), Units.Angle.DEGREE));
        assertEquals(2.5199999939650297, Units.convertTo(pos.latitude(), Units.Angle.DEGREE));
    }

    @Test
    void edTest3(){
        double x0, y0, x1, y1;
        x0 = scalb(111600d, -17);
        y0 = scalb(94445d, -17);
        x1 = scalb(108865d, -17);
        y1 = scalb(77558d, -17);
        GeoPos pos = CprDecoder.decodePosition(x0,y0,x1,y1,1);
        assertEquals(552647316,pos.latitudeT32());
        assertEquals(89182208,pos.longitudeT32());
    }

    @Test
    void bookTest(){
        double x0, y0, x1, y1;
        x0 = 51372d / 131072d;
        y0 = 93000d / 131072d;
        x1 = 50194d / 131072d;
        y1 = 74158d / 131072d;
        GeoPos pos1 = CprDecoder.decodePosition(x0,y0,x1,y1,1);
        GeoPos pos0 = CprDecoder.decodePosition(x0,y0,x1,y1,0);

        assertEquals(52.25720214843750,Units.convert(pos0.latitudeT32(),Units.Angle.T32,Units.Angle.DEGREE),Math.pow(10,-7));
        assertEquals(52.26578017412606,Units.convert(pos1.latitudeT32(),Units.Angle.T32,Units.Angle.DEGREE),Math.pow(10,-7));

        assertEquals(3.91937,Units.convert(pos0.longitudeT32(),Units.Angle.T32,Units.Angle.DEGREE),Math.pow(10,-5));

    }

    private static double cpr(double cpr) {
        return scalb(cpr, -17);
    }

    void checkDecodePosition(int cprX0,
                             int cprY0,
                             int cprX1,
                             int cprY1,
                             int mostRecent,
                             double expectedLonDeg,
                             double expectedLatDeg,
                             double delta) {
        var x0 = cpr(cprX0);
        var x1 = cpr(cprX1);
        var y0 = cpr(cprY0);
        var y1 = cpr(cprY1);
        var p = CprDecoder.decodePosition(x0, y0, x1, y1, mostRecent);
        assertNotNull(p);
        assertEquals(expectedLonDeg, toDegrees(p.longitude()), delta);
        assertEquals(expectedLatDeg, toDegrees(p.latitude()), delta);
    }

    @Test
    void cprDecoderDecodePositionWorksOnKnownExamples() {
        // Example given in stage 5
        var delta = 1e-6;
        checkDecodePosition(111600, 94445, 108865, 77558, 0, 7.476062, 46.323349, delta);

        // Example from https://mode-s.org/decode/content/ads-b/3-airborne-position.html#decoding-example
        checkDecodePosition(0b01100100010101100, 0b10110101101001000, 0b01100010000010010, 0b10010000110101110, 0, 3.919373, 52.257202, delta);

        // Examples from https://github.com/flightaware/dump1090/blob/master/cprtests.c
        checkDecodePosition(9432, 80536, 9192, 61720, 0, 0.700156, 51.686646, delta);
        checkDecodePosition(9432, 80536, 9192, 61720, 1, 0.701294, 51.686763, delta);
        checkDecodePosition(9413, 80534, 9144, 61714, 0, 0.698745, 51.686554, delta);
        checkDecodePosition(9413, 80534, 9144, 61714, 1, 0.697632, 51.686484, delta);
    }

    @Test
    void cprDecoderDecodePositionWorksWithOnlyOneLatitudeBand() {
        checkDecodePosition(2458, 92843, 2458, 60712, 0, 6.75, 88.25, 1e-2);
        checkDecodePosition(2458, 92843, 2458, 60712, 1, 6.75, 88.25, 1e-2);
    }

    @Test
    void cprDecoderDecodePositionWorksWithPositiveAndNegativeCoordinates() {
        for (var i = 0; i <= 1; i += 1) {
            checkDecodePosition(94663, 43691, 101945, 47332, i, -20d, -10d, 1e-4);
            checkDecodePosition(94663, 87381, 101945, 83740, i, -20d, 10d, 1e-4);
            checkDecodePosition(36409, 43691, 29127, 47332, i, 20d, -10d, 1e-4);
            checkDecodePosition(36409, 87381, 29127, 83740, i, 20d, 10d, 1e-4);
        }
    }

    @Test
    void cprDecoderDecodePositionReturnsNullWhenLatitudeIsInvalid() {
        assertNull(CprDecoder.decodePosition(0, 0, 0, cpr(34776), 0));
        assertNull(CprDecoder.decodePosition(0, 0, 0, cpr(34776), 1));
        assertNull(CprDecoder.decodePosition(0, cpr(5), 0, cpr(66706), 0));
        assertNull(CprDecoder.decodePosition(0, cpr(5), 0, cpr(66706), 1));
    }

    @Test
    void cprDecoderDecodePositionReturnsNullWhenSwitchingLatitudeBands() {
        var args = new int[][]{
                // Random values
                {43253, 99779, 122033, 118260},
                {67454, 100681, 123802, 124315},
                {129578, 70001, 82905, 105074},
                {30966, 110907, 122716, 79872},
                // Real values
                {85707, 77459, 81435, 60931},
                {100762, 106328, 98304, 89265},
                {104941, 106331, 104905, 89210},
        };

        for (var as : args) {
            var x0 = cpr(as[0]);
            var y0 = cpr(as[1]);
            var x1 = cpr(as[2]);
            var y1 = cpr(as[3]);
            assertNull(CprDecoder.decodePosition(x0, y0, x1, y1, 0));
            assertNull(CprDecoder.decodePosition(x0, y0, x1, y1, 1));
        }
    }
}