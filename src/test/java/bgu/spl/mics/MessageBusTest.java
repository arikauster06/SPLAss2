package bgu.spl.mics;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


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
        // added sleep, waiting for microservice to initialize
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        microServiceTest.sendEvent(new TestEvent());
//        microServiceTest.sendEvent(new TestEvent2());

        assertEquals(TestText, future.get());

        //Removing Terminate and unregister by "hand"
        //microServiceTest.terminate();
        t.interrupt();
        MessageBusImpl.getInstance().unregister(microServiceTest);
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
                    // Sending the actual event and not a new event
                    complete(c, TestText);
                });
            }
        };
        Thread t = new Thread(microServiceTest);
        t.start();
        // added sleep, waiting for microservice to initialize
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Future<String> future = microServiceTest.sendEvent(new TestEvent());
        assertEquals(TestText, future.get());

        // deleted terminate and unregistering by hand
        //microServiceTest.terminate();
        t.interrupt();
        MessageBusImpl.getInstance().unregister(microServiceTest);

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
        // adding sleep waiting for microservice to initialize
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        microServiceTest.sendBroadcast(new TestBroadcast());
        assertEquals(TestText, future.get());
        // Deleting terminate, unregistering by hand
//        microServiceTest.terminate();
        t.interrupt();
        MessageBusImpl.getInstance().unregister(microServiceTest);
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
        // added sleep, waiting for microservice to initialize
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        broadcastListener1.sendBroadcast(new TestBroadcast());
        assertEquals(TestText, future1.get());
        assertEquals(TestText2, future2.get());

        // Deleting terminate
//        broadcastListener1.terminate();
//        broadcastListener2.terminate();
        t1.interrupt();
        t2.interrupt();
        MessageBusImpl.getInstance().unregister(broadcastListener1);
        MessageBusImpl.getInstance().unregister(broadcastListener2);
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
        // added sleep, waiting for microservice to initialize
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        microServiceTest.terminate();

        EventSender.sendEvent(new TestEvent());

        String result = future.get(5, TimeUnit.SECONDS);
        assertNull(result);

        EventSender.terminate();

        MessageBusImpl.getInstance().unregister(microServiceTest);

    }
}
