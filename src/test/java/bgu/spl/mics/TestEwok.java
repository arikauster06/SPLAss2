package bgu.spl.mics;


import bgu.spl.mics.application.passiveObjects.Ewok;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestEwok {

    private Ewok ewok;

    @BeforeEach
    public void setup() {
        ewok = new Ewok(1);
    }


    @Test
    // Test ewok is available after initialized
    public void testisAvailable() {
        assertTrue(ewok.isAvailable());
    }

    @Test
    // Test acquiring an ewoke change it's state
    public void testAcquire() {
        ewok.acquire();
        assertFalse(ewok.isAvailable());
    }

    @Test
    // Test ewoke can be acquired and then release and return to be available again
    public void testRelease() {
        ewok.acquire();
        ewok.release();
        assertTrue(ewok.isAvailable());
    }
}
