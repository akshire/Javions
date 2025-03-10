package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public class PowerWindowTest {
    private static final int BATCH_SIZE = 1 << 16;
    private static final int BATCH_SIZE_BYTES = bytesForPowerSamples(BATCH_SIZE);
    private static final int STANDARD_WINDOW_SIZE = 1200;
    private static final int BIAS = 1 << 11;

    @Test
    void constructorTest() throws IOException{
        try(InputStream stream = new FileInputStream("resources/samples.bin")) {
            assertThrows(IllegalArgumentException.class, () -> {
                PowerWindow a = new PowerWindow(stream, (int)((Math.pow(2,16)) + 1));
            });
            assertThrows(IllegalArgumentException.class, () -> {
                PowerWindow a = new PowerWindow(stream, 0);
            });

            PowerWindow a = new PowerWindow(stream,1<<16);
            PowerWindow b = new PowerWindow(stream, (int)Math.pow(2,16));
        }
    }

    @Test
    void sizeTest() throws IOException{
        try(InputStream stream = new FileInputStream("resources/samples.bin")) {
            PowerWindow a = new PowerWindow(stream,1<<16);
            assertEquals(1<<16,a.size());
        }
    }

    @Test
    void positionIniTest()throws IOException{
        try(InputStream stream = new FileInputStream("resources/samples.bin")) {
            PowerWindow a = new PowerWindow(stream,1<<16);
            assertEquals(0,a.position());
        }
    }

    @Test
     void isFullIniTest()throws IOException{
        try(InputStream stream = new FileInputStream("resources/samples.bin")) {
            PowerWindow a = new PowerWindow(stream,1<<16);
            assertTrue(a.isFull());
        }
    }

    @Test
    void getTrivialTest()throws IOException{
        try(InputStream stream = new FileInputStream("resources/samples.bin")) {
            PowerWindow a = new PowerWindow(stream,1<<16);
            int[] b = {73, 292, 65, 745, 98, 4226, 12244, 25722,36818,23825};
            for (int i = 0; i < 10; i++) {
                assertEquals(b[i],a.get(i));
            }
        }
    }

    @Test
    void advanceTrivialTest()throws IOException{
        try(InputStream stream = new FileInputStream("resources/samples.bin")) {
            PowerWindow a = new PowerWindow(stream,2);
            int[] b = {73, 292, 65, 745, 98, 4226, 12244, 25722,36818,23825};
            for (int i = 0; i < 9; i++) {

                if(i>0){a.advance();}
                for(int j = 0; j<2;j++){

                    assertEquals(b[i+j],a.get(j));
                }

            }
        }
    }

    @Test
    void advanceMaxWindowLengthTest()throws IOException{
        try(InputStream stream = new FileInputStream("resources/samples.bin")) {
            PowerWindow a = new PowerWindow(stream,1 << 16);
            int[] b = {73, 292, 65, 745, 98, 4226, 12244, 25722,36818,23825};
            for (int i = 0 ; i < 10 ; i++) {
                if(i>0){a.advance();}
                for(int j = 0 ; j < 10 - i ; j++){
                    assertEquals(b[j + i],a.get( j ));
                }
            }
        }
    }

    @Test
    void getWithWindowLengthOne()throws IOException{
        try(InputStream stream = new FileInputStream("resources/samples.bin");
        InputStream streamTo = new FileInputStream("resources/samples.bin")) {
            PowerWindow a = new PowerWindow(stream,1);
            PowerComputer c = new PowerComputer(streamTo, 1<<16);
            int[] b = new int[1<<16];
            int e = c.readBatch(b);
            assertThrows(IndexOutOfBoundsException.class, ()->a.get(1));

            for(int i = 0; i<(1<<16); i++){
                if(i>0){a.advance();}
                assertEquals(b[i],a.get(0));
            }

            int[] f = new int[1<<16];
            c.readBatch(f);

            for(int i = 0; i< (1<<16); i++){
                a.advance();
                assertEquals(f[i],a.get(0));
            }

        }
    }

    @Test
    void getRandomWindowLengthSwitchBetweenTwoArraysTest()throws IOException{
        try(InputStream stream = new FileInputStream("resources/samples.bin");
            InputStream streamTo = new FileInputStream("resources/samples.bin")) {
            PowerWindow a = new PowerWindow(stream,6);

            int[] b = new int[(1<<16)*2];
            PowerComputer c = new PowerComputer(streamTo,(1<<16)*2);

            c.readBatch(b);

            for(int i = 0; i < (((1<<16)*2)-6);i++){
                if(i>0){a.advance();}
                for (int j = 0; j < 6; j++) {

                    assertEquals(b[i+j],a.get(j));

                }
            }
        }
    }

    @Test
    void getNadvanceTest() throws  IOException {
        try (InputStream inputStream = new FileInputStream("resources/samples.bin")){
            int position = 5;
            PowerWindow pwTest = new PowerWindow(inputStream, 4);
            pwTest.advanceBy(5);
            int[] array = new int[]{73, 292, 65, 745, 98, 4226, 12244, 25722, 36818, 23825};
            for (int i = 0; i < 4; ++i) {
                assertEquals(array[position], pwTest.get(i));
                position += 1;
            }
        }
    }

    @Test
        void switchingArraysTest()throws IOException{
        try (InputStream inputStream = new FileInputStream("resources/samples.bin")){
            int[] tab = new int[]{73,292,65,745,98,4226,12244,25722,36818,23825};
            PowerWindow powerWindow = new PowerWindow(inputStream,2);
            powerWindow.advanceBy(7);
            assertEquals(tab[8],powerWindow.get(1));

        }

    }
    //giga test a decomenter si vraiment pas sur

    @Test
    void allValuesOfBigFile()throws IOException{
        try (InputStream stream = new FileInputStream("resources/samples_20230304_1442.bin");
             InputStream stream2 = new FileInputStream("resources/samples_20230304_1442.bin")) {
            PowerComputer a = new PowerComputer(stream,30000000);
            int[] b = new int[30000000];
            a.readBatch(b);
            PowerWindow powerWindow = new PowerWindow(stream2,1200);
            for (int i = 0; i < 30000000-1200; i++) {
                if(i>0){powerWindow.advance();}
                for (int j = 0; j < 1200; j++) {
                    if((b[i+j]!=powerWindow.get(j))){
                        System.out.println(i+j);
                    }
                    assertEquals(b[i+j],powerWindow.get(j));
                }
            }
        }
    }

    private static int bytesForPowerSamples(int powerSamplesCount) {
        return powerSamplesCount * 2 * Short.BYTES;
    }

    @Test
    void powerWindowConstructorThrowsWithInvalidWindowSize() throws IOException {
        try (var s = InputStream.nullInputStream()) {
            assertThrows(IllegalArgumentException.class, () -> new PowerWindow(s, 0));
            assertThrows(IllegalArgumentException.class, () -> new PowerWindow(s, -1));
            assertThrows(IllegalArgumentException.class, () -> new PowerWindow(s, (1 << 16) + 1));
        }
    }

    @Test
    void powerWindowSizeReturnsWindowSize() throws IOException {
        try (var s = InputStream.nullInputStream()) {
            for (var i = 1; i <= 1 << 16; i <<= 1) {
                var w = new PowerWindow(s, i);
                assertEquals(i, w.size());
            }
        }
    }

    @Test
    void powerWindowPositionIsCorrectlyUpdatedByAdvance() throws IOException {
        var batches16 = new byte[BATCH_SIZE_BYTES * 16];
        try (var s = new ByteArrayInputStream(batches16)) {
            var w = new PowerWindow(s, STANDARD_WINDOW_SIZE);
            var expectedPos = 0L;

            assertEquals(expectedPos, w.position());

            w.advance();
            expectedPos += 1;
            assertEquals(expectedPos, w.position());

            w.advanceBy(BATCH_SIZE);
            expectedPos += BATCH_SIZE;
            assertEquals(expectedPos, w.position());

            w.advanceBy(BATCH_SIZE - 1);
            expectedPos += BATCH_SIZE - 1;
            assertEquals(expectedPos, w.position());

            w.advance();
            expectedPos += 1;
            assertEquals(expectedPos, w.position());
        }
    }

    @Test
    void powerWindowAdvanceByCanAdvanceOverSeveralBatches() throws IOException {
        var bytes = bytesForZeroSamples(16);

        var batchesToSkipOver = 2;
        var inBatchOffset = 37;
        var offset = batchesToSkipOver * BATCH_SIZE + inBatchOffset;
        var sampleBytes = Base64.getDecoder().decode(PowerComputerTest.SAMPLES_BIN_BASE64);
        System.arraycopy(sampleBytes, 0, bytes, bytesForPowerSamples(offset), sampleBytes.length);

        try (var s = new ByteArrayInputStream(bytes)) {
            var w = new PowerWindow(s, STANDARD_WINDOW_SIZE);
            w.advanceBy(inBatchOffset);
            w.advanceBy(batchesToSkipOver * BATCH_SIZE);
            var expected = Arrays.copyOfRange(PowerComputerTest.POWER_SAMPLES, 0, STANDARD_WINDOW_SIZE);
            var actual = new int[STANDARD_WINDOW_SIZE];
            for (var i = 0; i < STANDARD_WINDOW_SIZE; i += 1) actual[i] = w.get(i);
            assertArrayEquals(expected, actual);
        }
    }

    @Test
    void powerWindowIsFullWorks() throws IOException {
        var twoBatchesPlusOneWindowBytes =
                bytesForPowerSamples(BATCH_SIZE * 2 + STANDARD_WINDOW_SIZE);
        var twoBatchesPlusOneWindow = new byte[twoBatchesPlusOneWindowBytes];
        try (var s = new ByteArrayInputStream(twoBatchesPlusOneWindow)) {
            var w = new PowerWindow(s, STANDARD_WINDOW_SIZE);
            assertTrue(w.isFull());

            w.advanceBy(BATCH_SIZE);
            assertTrue(w.isFull());

            w.advanceBy(BATCH_SIZE);
            assertTrue(w.isFull());

            w.advance();
            assertFalse(w.isFull());
        }
    }

    @Test
    void powerWindowGetWorksOnGivenSamples() throws IOException {
        try (var sampleStream = PowerComputerTest.getSamplesStream()) {
            var windowSize = 100;
            var w = new PowerWindow(sampleStream, windowSize);
            for (var offset = 0; offset < 100; offset += 1) {
                var expected = Arrays.copyOfRange(PowerComputerTest.POWER_SAMPLES, offset, offset + windowSize);
                var actual = new int[windowSize];
                for (var i = 0; i < windowSize; i += 1) actual[i] = w.get(i);
                assertArrayEquals(expected, actual);
                w.advance();
            }
        }
    }

    @Test
    void powerWindowGetWorksAcrossBatches() throws IOException {
        byte[] bytes = bytesForZeroSamples(2);
        var firstBatchSamples = STANDARD_WINDOW_SIZE / 2 - 13;
        var offset = BATCH_SIZE_BYTES - bytesForPowerSamples(firstBatchSamples);
        var sampleBytes = Base64.getDecoder().decode(PowerComputerTest.SAMPLES_BIN_BASE64);
        System.arraycopy(sampleBytes, 0, bytes, offset, sampleBytes.length);
        try (var s = new ByteArrayInputStream(bytes)) {
            var w = new PowerWindow(s, STANDARD_WINDOW_SIZE);
            w.advanceBy(BATCH_SIZE - firstBatchSamples);
            for (int i = 0; i < STANDARD_WINDOW_SIZE; i += 1)
                assertEquals(PowerComputerTest.POWER_SAMPLES[i], w.get(i));
        }
    }

    private static byte[] bytesForZeroSamples(int batchesCount) {
        var bytes = new byte[BATCH_SIZE_BYTES * batchesCount];

        var msbBias = BIAS >> Byte.SIZE;
        var lsbBias = BIAS & ((1 << Byte.SIZE) - 1);
        for (var i = 0; i < bytes.length; i += 2) {
            bytes[i] = (byte) lsbBias;
            bytes[i + 1] = (byte) msbBias;
        }
        return bytes;
    }
}
