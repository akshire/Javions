package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.aircraft.IcaoAddress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public class RawMessageTest {

    @Test
    void constructorTest(){
        assertThrows(IllegalArgumentException.class,()->{
            RawMessage a = new RawMessage(-1,null);
        });

        assertThrows(IllegalArgumentException.class,()->{
            RawMessage b = new RawMessage(42, new ByteString(new byte[23]));
        });
        //attention si ByteString null alors il n'y a pas d'erreur lancée
    }

    @Test
    void sizeTest(){
        //les 5 premiers bits valent 17
        byte b = 0b10001;
        b = (byte)(b<<3);
        assertEquals(RawMessage.LENGTH,RawMessage.size(b));

        //autre test, juste en changeant la fin du byte
        b = (byte)(b|0b111);
        assertEquals(RawMessage.LENGTH,RawMessage.size(b));
    }

    @Test
    void typeCodeStaticTest(){
        long payLoad = 0b01111;
        payLoad = payLoad <<51;
        assertEquals(0b01111,RawMessage.typeCode(payLoad));

        long payLoad2 = 0b11111;
        payLoad2 = (payLoad2<<51)|0b1111111111111111111111111;
        assertEquals(0b11111,RawMessage.typeCode(payLoad2));
    }

    @Test
    void downLinkFormatTest(){
        RawMessage a = new RawMessage(8096200,ByteString.ofHexadecimalString("8D4B17E5F8210002004BB8B1F1AC"));
        

    }

    @Test
    void icaoAdressTest(){
        RawMessage a = new RawMessage(8096200,ByteString.ofHexadecimalString("8D4B17E5F8210002004BB8B1F1AC"));
        assertEquals(new IcaoAddress("4B17E5"),a.icaoAddress());

        RawMessage b = new RawMessage(75898000,ByteString.ofHexadecimalString("8D49529958B302E6E15FA352306B"));
        assertEquals( new IcaoAddress("495299"),b.icaoAddress());
    }

    @Test
    void payloadTest(){
        RawMessage a = new RawMessage(8096200,ByteString.ofHexadecimalString("8D4B17E5F8210002004BB8B1F1AC"));
        assertEquals(0xF8210002004BB8L,a.payload());
    }

    @Test
    void typeCodeTest(){
        RawMessage a = new RawMessage(8096200,ByteString.ofHexadecimalString("8D4B17E5F8210002004BB8B1F1AC"));
        assertEquals(31,a.typeCode());
    }









}
