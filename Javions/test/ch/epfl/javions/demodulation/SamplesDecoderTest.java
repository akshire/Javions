package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public class SamplesDecoderTest {
    @Test
    void readBachTest(){

        try(InputStream stream = new FileInputStream("resources/samples.bin")){
            SamplesDecoder i = new SamplesDecoder(stream,10);
            short[] a = new short[10];

            short[]b = {-3,8,-9,-8,-5,-8,-12,-16,-23,-9};
            int j = i.readBatch(a);
            assertEquals(10,j);
            for(int k = 0; k<10;k++){
                assertEquals(b[k],a[k]);
            }

        }catch(IOException e) {
            System.out.println("PAS DE FICHIER");
        }
    }

    @Test
    void readBatchToEndOfFile(){
        try(InputStream inputStream = new FileInputStream("resources/samples.bin")){
            short[] tabShort = new short[2500];
            SamplesDecoder o = new SamplesDecoder(inputStream, 2500);
            assertEquals(2402,o.readBatch(tabShort));

        }catch(IOException e){assertTrue(false);}
    }
}
