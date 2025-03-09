package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Erik Hübner (341205)
 * @author Phuc-hung Truong (345674)
 */

public class WakeTurbulenceCategoryTest {
    @Test
    void returnsHeavy(){
        assertEquals(WakeTurbulenceCategory.HEAVY,WakeTurbulenceCategory.of("H"));
    }
    @Test
    void returnsLight(){
        assertEquals(WakeTurbulenceCategory.LIGHT,WakeTurbulenceCategory.of("L"));
    }

    @Test
    void returnsMedium(){
        assertEquals(WakeTurbulenceCategory.MEDIUM,WakeTurbulenceCategory.of("M"));
    }

    @Test
    void returnsNone(){
        assertEquals(WakeTurbulenceCategory.UNKNOWN,WakeTurbulenceCategory.of("Hi"));
        assertEquals(WakeTurbulenceCategory.UNKNOWN,WakeTurbulenceCategory.of("nvclkjasnvl"));
        assertEquals(WakeTurbulenceCategory.UNKNOWN,WakeTurbulenceCategory.of(""));
    }
}
