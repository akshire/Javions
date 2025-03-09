package ch.epfl.javions;

import org.junit.jupiter.api.Test;

import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Erik Hübner (341205)
 */
public class Crc24Test {

    @Test
    void bit_wiseTestVerification(){
        Crc24 crc24 = new Crc24(Crc24.GENERATOR);


        String mS1 = "8D392AE499107FB5C00439";
        String cS1 = "035DB8";
        String mS2 ="8D4D2286EA428867291C08";
        String cS2 ="EE2EC6";
        String mS3 = "8D3950C69914B232880436";
        String cS3 = "BC63D3";
        String mS4 = "8D4B17E399893E15C09C21";
        String cS4 = "9FC014";
        String mS5 ="8D4B18F4231445F2DB63A0";
        String cS5 ="DEEB82";
        String mS6 ="8D495293F82300020049B8";
        String cS6 ="111203";

        byte[] mAndC1 = HexFormat.of().parseHex(mS1 + cS1);
        byte[] mAndC2 = HexFormat.of().parseHex(mS2 + cS2);
        byte[] mAndC3 = HexFormat.of().parseHex(mS3 + cS3);
        byte[] mAndC4 = HexFormat.of().parseHex(mS4 + cS4);
        byte[] mAndC5 = HexFormat.of().parseHex(mS5 + cS5);
        byte[] mAndC6 = HexFormat.of().parseHex(mS6 + cS6);

        assertEquals(0, crc24.crc(mAndC1));
        assertEquals(0, crc24.crc(mAndC2));
        assertEquals(0, crc24.crc(mAndC3));
        assertEquals(0, crc24.crc(mAndC4));
        assertEquals(0, crc24.crc(mAndC5));
        assertEquals(0, crc24.crc(mAndC6));


    }

    @Test
    void bitwiseTestComputeCrc(){
        Crc24 crc24 = new Crc24(Crc24.GENERATOR);

        String mS1 = "8D392AE499107FB5C00439";
        String cS1 = "035DB8";
        String mS2 ="8D4D2286EA428867291C08";
        String cS2 ="EE2EC6";
        String mS3 = "8D3950C69914B232880436";
        String cS3 = "BC63D3";
        String mS4 = "8D4B17E399893E15C09C21";
        String cS4 = "9FC014";
        String mS5 ="8D4B18F4231445F2DB63A0";
        String cS5 ="DEEB82";
        String mS6 ="8D495293F82300020049B8";
        String cS6 ="111203";

        int c1 = Integer.parseInt(cS1, 16);
        int c2 = Integer.parseInt(cS2, 16);
        int c3 = Integer.parseInt(cS3, 16);
        int c4 = Integer.parseInt(cS4, 16);
        int c5 = Integer.parseInt(cS5, 16);
        int c6 = Integer.parseInt(cS6, 16);


        byte[] mOnly1 = HexFormat.of().parseHex(mS1);
        byte[] mOnly2 = HexFormat.of().parseHex(mS2);
        byte[] mOnly3 = HexFormat.of().parseHex(mS3);
        byte[] mOnly4 = HexFormat.of().parseHex(mS4);
        byte[] mOnly5 = HexFormat.of().parseHex(mS5);
        byte[] mOnly6 = HexFormat.of().parseHex(mS6);


        assertEquals(c1, crc24.crc(mOnly1));
        assertEquals(c2, crc24.crc(mOnly2));
        assertEquals(c3, crc24.crc(mOnly3));
        assertEquals(c4, crc24.crc(mOnly4));
        assertEquals(c5, crc24.crc(mOnly5));
        assertEquals(c6, crc24.crc(mOnly6));

    }

}
