package bgu.spl.mics;

import bgu.spl.mics.Event;
import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.messages.AttackEvent;
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


public class MessageBusTest {

    public static class TestEvent implements Event<String> {
    }

    public static class TestBroadcast implements Broadcast {

    }

    @Test
    // check the start of an event flow:
    // creates a new microservice and check that when running it will register itself to the
    // message bus - which creates it's queue, then the microservice being intializes
    // and subscirbes event. afterwards the micriservice sends event to the bus message
    // which allocate this event to the microservice - which is registered to handle this
    // event. the microservice calls the "awaitMassage" to fetcha message from it's queue
    // and resolve this message's event.
    // overall in this test we've tested the functionality of tthe functions:
    // register, sendEvent, subscribeEvent and awaitMessage.

    public void testBasics() {
        String TestText = "Subscribed successfully";
        Future<String> future = new Future<>();
        MicroService microServiceTest = new MicroService("Rick") {
            @Override
            protected void initialize() {
                subscribeEvent(TestEvent.class, c -> {
                    future.resolve(TestText);
                });
            }
        };
        Thread t = new Thread(microServiceTest);
        t.start();
        microServiceTest.sendEvent(new TestEvent());
        assertEquals(TestText, future.get());

        microServiceTest.terminate();
    }

    @Test
    // this test checks a bit more of the process, it tests also the tests that we've already covered
    // in the first one () and here we continue to test the rest of the process, which include testing the
    // "complete" function and making sure that the sendEvent update "future" with the correct value.
    public void testUsingComplete() {
        String TestText = "Subscribed successfully";
        MicroService microServiceTest = new MicroService("Rick") {
            @Override
            protected void initialize() {
                subscribeEvent(TestEvent.class, c -> {
                    complete(new TestEvent(), TestText);
                });
            }
        };
        Thread t = new Thread(microServiceTest);
        t.start();
        Future<String> future = microServiceTest.sendEvent(new TestEvent());
        assertEquals(TestText, future.get());

        microServiceTest.terminate();
    }

    @Test
    // As before, we test the lifecycle of a microservice, checks that it listen to broadcast
    // and act onb them
    // overall in this test we've tested the functionality of tthe functions:
    // register, sendBroadcast, subscribeBroadcast and awaitMessage.
    public void testBasicBroadcast() {
        String TestText = "Broadcast Subscribed successfully";
        Future<String> future = new Future<>();
        MicroService microServiceTest = new MicroService("Rick") {
            @Override
            protected void initialize() {
                subscribeBroadcast(TestBroadcast.class, c -> {
                    future.resolve(TestText);
                });
            }
        };
        Thread t = new Thread(microServiceTest);
        t.start();
        microServiceTest.sendBroadcast(new TestBroadcast());
        assertEquals(TestText, future.get());
        microServiceTest.terminate();
    }

    @Test
    // Test that 2 microservices can subscribe to the same broadcast event, and both act on them
    public void test2BroadcastListeners() {
        String TestText = "Broadcast1";
        String TestText2 = "Broadcast2";
        Future<String> future1 = new Future<>();
        Future<String> future2 = new Future<>();
        MicroService broadcastListener1 = new MicroService("Rick") {
            @Override
            protected void initialize() {
                subscribeBroadcast(TestBroadcast.class, c -> {
                    future1.resolve(TestText);
                });
            }
        };
        MicroService broadcastListener2 = new MicroService("Morty") {
            @Override
            protected void initialize() {
                subscribeBroadcast(TestBroadcast.class, c -> {
                    future2.resolve(TestText2);
                });
            }
        };
        Thread t1 = new Thread(broadcastListener1);
        Thread t2 = new Thread(broadcastListener2);
        t1.start();
        t2.start();

        broadcastListener1.sendBroadcast(new TestBroadcast());
        assertEquals(TestText, future1.get());
        assertEquals(TestText2, future2.get());

        broadcastListener1.terminate();
        broadcastListener2.terminate();
    }

    //unregister
    @Test
    // Test that if a microservice is unregister from the bus, it won't act upon events.
    public void testUnregister() {
        String TestText = "Subscribed successfully";
        Future<String> future = new Future<>();
        MicroService microServiceTest = new MicroService("Rick") {
            @Override
            protected void initialize() {
                subscribeEvent(TestEvent.class, c -> {
                    future.resolve(TestText);
                });
            }
        };

        MicroService EventSender = new MicroService("Sender") {
            @Override
            protected void initialize() {

            }
        };

        Thread t = new Thread(microServiceTest);
        t.start();

        microServiceTest.terminate();

        EventSender.sendEvent(new TestEvent());

        boolean isExceptionThrowned = false;
        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            isExceptionThrowned = true;
        }
        assertTrue(isExceptionThrowned);


    }
}
