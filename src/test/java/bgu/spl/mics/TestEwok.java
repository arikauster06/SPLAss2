package bgu.spl.mics;


import bgu.spl.mics.Event;
import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Ewok;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.experimental.theories.*;
import org.junit.runner.RunWith;

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
