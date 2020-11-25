package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;


import static org.junit.jupiter.api.Assertions.*;
public class FutureTest {

    private Future<String> future;

    public static final int TIME_TO_SLEEP = 2000;


    @BeforeEach
    public void setUp() {
        future = new Future<>();
    }

    //get Tests
    @Test
    // Test that get method work
    // calling the get (which wil wait for the future to resolve)
    // on another thread (because the main is blocked) we initialize a thread with sleep command
    // (waiting for the future.get to occur.
    // and then resolving the future, so get method will exit the block.
    // afterwards we terminite the thread so we could test if the get method actually continue after
    // resolving the future, or didn't behave on continued independently.
    public void testGet() {
        String value = "value";

        AtomicLong ResolvedTS = new AtomicLong(0);
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(TIME_TO_SLEEP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!Thread.interrupted()) {
                    ResolvedTS.set(System.currentTimeMillis());
                    future.resolve(value);

                }
            }
        };
        t.start();
        String getOutput = future.get();
        assertEquals(getOutput, value);
        long afterGetTimestamp = System.currentTimeMillis();
        t.interrupt();
        try {
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Test if resolve executed, if not, the value to the ResolveTS should be
        // remain 0
        assertNotEquals(0, ResolvedTS.get());

        // If not zero,
        // Test if the get method didn't exit before the resolve actually occurred
        assertTrue(ResolvedTS.get() < afterGetTimestamp);
    }

    @Test
    // Test using get twice won't change the result.
    public void testGetTwice() {
        String str = "someResult";
        future.resolve(str);
        assertEquals(str, future.get());
        assertEquals(str, future.get());
    }


    // Resolve Tests
    @Test
    public void testResolve() {
        String str = "someResult";
        future.resolve(str);
        assertTrue(future.isDone());
        assertEquals(str, future.get());
    }

    @Test
    // Test that future can assigned with new value
    public void testResolveTwiceDifferentValues() {
        String value1 = "value1";
        String value2 = "value2";

        future.resolve(value1);
        assertEquals(value1, future.get());
        //assign new value
        future.resolve(value2);
        assertEquals(value2, future.get());
    }

    @Test
    // Test that resolving twice with the same value won't harm the future
    public void testResolveTwiceSameValue() {
        String value1 = "value1";
        future.resolve(value1);
        assertEquals(value1, future.get());

        //assign new value
        future.resolve(value1);
        assertEquals(value1, future.get());
    }

    @Test
    // Test resolving with null value
    // no specified behavior supplied so we guessed it should won't crash.
    public void testResolveWithNullValue() {
        future.resolve(null);

        assertTrue(future.isDone());
    }

    // Is Done Tests
    @Test
    // Test isDone is initialized with false value
    public void testFutureIsDoneFalseAtStart() {
        assertFalse(future.isDone());
    }

    @Test
    // Test that resolved future won't change its' state
    public void testResolveTwiceAffectsIsDone() {
        String value1 = "value1";
        String value2 = "value2";

        future.resolve(value1);
        assertTrue(future.isDone());
        //assign new value
        future.resolve(value2);
        assertTrue(future.isDone());
    }

    @Test
    // Testing that calling get will not change the isDone state of the future
    // making sure that the state only change by resolving
    public void testGetDoesNotAffectsisDone() {
        AtomicBoolean isDone = new AtomicBoolean(true);

        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                isDone.set(future.isDone());
                future.resolve("value");
            }
        }.start();
        future.get();

        assertFalse(isDone.get());
    }

    //Tests for get with timeout
    @Test
    // Test that get (with timeout) actually returning the expected result value
    public void getTimeoutReturnsCorrectValue() {
        String str = "someResult";
        future.resolve(str);
        assertTrue(future.isDone());
        assertEquals(str, future.get(1000, TimeUnit.SECONDS));
    }

    @Test
    public void testGetWithTimeoutWaits() {
        String value = "value";

        AtomicLong ResolvedTS = new AtomicLong(0);
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!Thread.interrupted()) {
                    ResolvedTS.set(System.currentTimeMillis());
                    future.resolve(value);

                }
            }
        };
        t.start();
        String getOutput = future.get(10, TimeUnit.SECONDS);
        assertEquals(getOutput, value);
        long afterGetTimestamp = System.currentTimeMillis();
        t.interrupt();
        try {
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Test if resolve executed, if not, the value to the ResolveTS should be
        // remain 0
        assertNotEquals(0, ResolvedTS.get());

        // If not zero,
        // Test if the get method didn't exit before the resolve actually occurred
        assertTrue(ResolvedTS.get() < afterGetTimestamp);
    }

    @Test
    // Testing that get actually exits after the timeout specified.
    public void testGetTimeout() {
        long beforeGetTS = System.currentTimeMillis();
        future.get(4, TimeUnit.SECONDS);
        long afterGetTS = System.currentTimeMillis();

        long timeDiff = afterGetTS - beforeGetTS;
        assertTrue(timeDiff >= 4000 & timeDiff < 5000);

    }

    //in case that future resolved - test that the get function doesn't wait for
    // the end of it's timeout inorder to return the result.
    @Test
    public void testGetTimeoutDoesntWaitIfResolved() {
        String value = "resault";
        future.resolve(value);
        long beforeGetTS = System.currentTimeMillis();
        future.get(10, TimeUnit.SECONDS);
        long afterGetTS = System.currentTimeMillis();

        long timeDiff = afterGetTS - beforeGetTS;
        assertTrue(timeDiff <= 2000);

    }

}
