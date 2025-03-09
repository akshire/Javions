package ch.epfl.javions.gui;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */
public final class ColorRampTest {

    @Test
    void ConstructorList(){
        ColorRamp a = new ColorRamp(List.of(Color.AQUA));
    }
}
