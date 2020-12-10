package bgu.spl.mics;

import bgu.spl.mics.*;
import bgu.spl.mics.application.Main;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Tester {
    //Coded By Ron Rachev
    //HF&GL
    /*
    Parses json info of a given test in a specified filepath and returns an array of test json objects
    */
    private static Test[] getTestsFromJson(String filePath) throws IOException {
        Gson gson = new Gson();
        try (Reader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, Test[].class);
        }
    }

    /*
    Converts a given object to Json and writes it into a specific file path
     */
    private void saveOutputToJson(String filePath, Object objectToConvert) {
        Gson testBuilderJson = new GsonBuilder().create();
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            testBuilderJson.toJson(objectToConvert, fileWriter);
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception fileWriteException) {

        }
    }

    /*
    Responsible for generation of tests
    id - id of test
    */
    private Test generateTest(int id) {
        Test generatedTest = new Test();
        Attack[] generatedAttacks;
        ArrayList<Integer> serialsUsedInAttack;

        //Lower limit for number of ewoks to use
        int numEwoksLowerLimit = 4;
        //Upper limit for number of ewoks to use
        int numEwoksUpperLimit = 7;
        int numEwoks = (int) (Math.random() * (numEwoksUpperLimit) + numEwoksLowerLimit);
        //Lower limit for number of attacks to generate
        int numAttacksLowerLimit = 3;
        //Upper limit for number of attacks to generate
        int numAttacksUpperLimit = 5;
        int numAttacks = (int) (Math.random() * (numAttacksUpperLimit) + numAttacksLowerLimit);
        generatedAttacks = new Attack[numAttacks];

        //Generate Random Attacks
        for (int i = 0; i < numAttacks; i++) {
            serialsUsedInAttack = new ArrayList<>();
            for (int r = 1; r <= numEwoks; r++) {
                //Coin Toss For Each Serial
                if ((int) (Math.random() * (2) + 1) == 1)
                    serialsUsedInAttack.add(r);
            }
            if (serialsUsedInAttack.isEmpty()) serialsUsedInAttack.add(1);
            generatedAttacks[i] = new Attack(serialsUsedInAttack, (int) (Math.random() * (300) + 100));
        }
        generatedTest.setAttacks(generatedAttacks);
        generatedTest.setEwoks(numEwoks);
        generatedTest.setLando((int) (Math.random() * (200) + 100));
        generatedTest.setR2D2((int) (Math.random() * (200) + 100));
        generatedTest.setTestId(id);
        generatedTest.setNumOfAttacks(generatedAttacks.length);
        //Set generatedTest vals so we can save it easily via the class reference
        System.out.println("\r\nDone Generating Test --> " + id);
        System.out.println("----------------------------------------\r\n");

        return generatedTest;
    }

    //Generates numOfTestsToGenerate Tests
    public void generateTests() {
        //Number of tests that are generated each time
        int numOfTestsToGenerate = 20;
        Test[] randTests = new Test[numOfTestsToGenerate];
        for (int i = 0; i < numOfTestsToGenerate; i++)
            randTests[i] = generateTest(i);
        saveOutputToJson("Tests.json", randTests);
    }

    //Returns minimum long from 4 inputs
    long FindMin(long a, long b, long c, long d) {
        long min1 = (a > b) ? b : a;
        long min2 = (c > d) ? d : c;
        return (min1 > min2) ? min2 : min1;
    }

    public void runTestsFromFile() {
        int passedTests = 0;
        int failedTests = 0;
        Test currentTests[];
        Diary diaryInstance = Diary.getInstance();
        try {
            currentTests = getTestsFromJson("Tests.json");
            for (int i = 0; i < currentTests.length; i++) {
                saveOutputToJson("input.json", currentTests[i]);
                //An output file shall be only created whenever the program finishes it's logic.
                File outputFile = new File("Output.json");
                if (outputFile.exists()) outputFile.delete();
                String filePath = "C:\\Users\\nadav\\Documents\\SPL\\Work2\\SPL211\\Tests.json";
                String outputPath = "C:\\Users\\nadav\\Documents\\SPL\\Work2\\SPL211\\output.json";
                String[] arg = {filePath, outputPath};
                Main.main(null);
                do {
                    Thread.sleep(10);
                } while (!outputFile.exists());
                System.out.println("\r\n\r\nFinished Test --- > " + i);
                AtomicInteger numOfAttacksInTest = diaryInstance.getTotalAttacks();
                long startingTimeOfTest = Math.max(diaryInstance.getC3POFinish(), diaryInstance.getHanSoloFinish());
                long shieldDeactivationTestValue = (diaryInstance.getR2D2Deactivate() - startingTimeOfTest);
                System.out.println("\r\n-----------------------------------");

                boolean passedFirstTest = false; //Checking Deactivation Shield Logic
                boolean passedSecondTest = false; //Checking Num Of Attacks Logic
                boolean passedThirdTest = true;  //Checking Graceful Termination (Should be at the same mili second~)

                System.out.println("Your Deactivation Shield Finished Time --> " + shieldDeactivationTestValue + "  Test Value Should Of Been -> " + currentTests[i].getR2D2Sleep());
                //if (Math.round(shieldDeactivationTestValue / 100) * 100 == (Math.round(currentTests[i].getR2D2Sleep()) / 100) * 100)
                if (Math.abs(shieldDeactivationTestValue - currentTests[i].getR2D2Sleep()) < 10)
                    passedFirstTest = true;
                if (numOfAttacksInTest.get() == (currentTests[i].getNumberOfAttacks().get()))
                    passedSecondTest = true;

                long soloTerminate = (diaryInstance.getHanSoloTerminate());
                long C3POTerminate = (diaryInstance.getC3POTerminate());
                long LandoTermiante = (diaryInstance.getLandoTerminate());
                long R2D2Terminate = (diaryInstance.getR2D2Terminate());

                long minTerminate = FindMin(soloTerminate, C3POTerminate, LandoTermiante, R2D2Terminate);
                System.out.println("Minimum Termination Time --> " + minTerminate);

                //Termination Difference shall not be bigger than 0000000000020L, Otherwise an invalid method of termination is used (Most likely)
                if (soloTerminate - minTerminate > 0000000000020L && C3POTerminate - minTerminate > 0000000000020L
                        && LandoTermiante - minTerminate > 0000000000020L && R2D2Terminate - minTerminate > 0000000000020L)
                    passedThirdTest = false;

                if (passedFirstTest && passedSecondTest && passedThirdTest) {
                    passedTests++;
                    System.out.println("First: " + passedFirstTest);
                    System.out.println("Second: " + passedSecondTest);
                    System.out.println("Third: " + passedThirdTest);
                    System.out.println("Passed Test --> " + i);
                } else {
                    System.out.println("First: " + passedFirstTest);
                    System.out.println("Second: " + passedSecondTest);
                    System.out.println("Third: " + passedThirdTest);
                    System.out.println("Failed Test --> " + i);
                    failedTests++;
                }
                System.out.println("\r\n");
                diaryInstance.resetNumberAttacks();
            }
            System.out.println("\r\n-----------------");
            System.out.println("Success->" + passedTests);
            System.out.println("Failed->" + failedTests);
        } catch (Exception runTestException) {
            runTestException.printStackTrace();
        }
    }


    /*
    Checks for various logical scenarios
    Multithreads/syncing etc.
    For e.x: Roundrobin  etc.
    */

    public class Event1 implements Event<Boolean> {
    }

    public class Event2 implements Event<Boolean> {
    }

    public class Event3 implements Event<String> {
    }

    public class Broadcast1 implements Broadcast {
    }

    public class TestMicroServer extends MicroService {
        private CountDownLatch initialize;
        private CountDownLatch terminate;

        public TestMicroServer(String name, CountDownLatch initialize, CountDownLatch terminate) {
            super(name);
            this.initialize = initialize;
            this.terminate = terminate;
        }

        @Override
        protected void initialize() {
            subscribeBroadcast(Broadcast1.class, ev -> {
                terminate();
                terminate.countDown();
            });
            subscribeEvent(Event3.class, ev -> {
                complete(ev, getName());
            });
            initialize.countDown();

        }
    }

    public class SenderMicroServer extends MicroService {
        private CountDownLatch terminateSend;
        private CountDownLatch terminate;

        public SenderMicroServer(String name, CountDownLatch terminateSend, CountDownLatch terminate) {
            super(name);
            this.terminate = terminate;
            this.terminateSend = terminateSend;
        }

        @Override
        protected void initialize() {
            subscribeBroadcast(Broadcast1.class, ev -> {
                terminate();
                terminate.countDown();
            });
            Future<String> future = sendEvent(new Event3());
            if (future == null)
                System.out.println("no MicroServer is registered to the event");
            else {
                String result = future.get();
                if (result.equals("M1"))
                    numberOfM1.getAndIncrement();
                else if (result.equals("M2"))
                    numberOfM2.getAndIncrement();
                else numberOfM3.getAndIncrement();
            }
            terminateSend.countDown();
        }
    }

    static AtomicInteger numberOfM1 = new AtomicInteger(0);
    static AtomicInteger numberOfM2 = new AtomicInteger(0);
    static AtomicInteger numberOfM3 = new AtomicInteger(0);

    public void runLogicalTests() {
        try {
            MicroService hanSoloObj;
            MicroService C3POObj;

            boolean passedFirstTest = true;
            boolean passedSecondTest = true;
            hanSoloObj = new HanSoloMicroservice();
            C3POObj = new C3POMicroservice();

            MessageBusImpl messageInstance = MessageBusImpl.getInstance();

            messageInstance.register(hanSoloObj);
            messageInstance.register(C3POObj);

            //RoundRobin Test
            messageInstance.subscribeEvent(Event1.class, hanSoloObj);
            messageInstance.subscribeEvent(Event1.class, C3POObj);

            ArrayList<Event> eventsSentToHanSolo = new ArrayList<>();
            ArrayList<Event> eventsSentToC3PO = new ArrayList<>();
            ArrayList<Future> futuresHan = new ArrayList<>();
            ArrayList<Future> futuresC3PO = new ArrayList<>();
            ArrayList<Message> acquiredMessagesHan = new ArrayList<>();
            ArrayList<Message> acquiredMessagesC3PO = new ArrayList<>();

            final int numOfEventsToSend = 100;
            for (int i = 0; i < numOfEventsToSend; i++) {
                Event1 event1 = new Event1();
                Event1 event2 = new Event1();
                Future<Boolean> eventResult1 = messageInstance.sendEvent(event1);
                Future<Boolean> eventResult2 = messageInstance.sendEvent(event2);
                futuresHan.add(eventResult1);
                futuresC3PO.add(eventResult2);
                eventsSentToHanSolo.add(event1);
                eventsSentToC3PO.add(event2);
            }
            for (int i = 0; i < numOfEventsToSend; i++) {
                Message messageFromHan = messageInstance.awaitMessage(hanSoloObj);
                Message messageFromC3PO = messageInstance.awaitMessage(C3POObj);
                acquiredMessagesC3PO.add(messageFromC3PO);
                acquiredMessagesHan.add(messageFromHan);
            }
            for (int i = 0; i < numOfEventsToSend; i++) {
                messageInstance.complete(eventsSentToHanSolo.get(i), true); //simple check for complete method
                messageInstance.complete(eventsSentToC3PO.get(i), true);
            }
            for (int i = 0; i < numOfEventsToSend - 1; i++) {
                if (!futuresHan.get(i).isDone() || !futuresC3PO.get(i).isDone())
                    passedFirstTest = false;
            }
            for (int i = 0; passedFirstTest && i < numOfEventsToSend; i++) {
                if (acquiredMessagesHan.get(i) != eventsSentToHanSolo.get(i) && acquiredMessagesC3PO.get(i) != eventsSentToC3PO.get(i)) {
                    System.out.println("Failed Round Robin in loop");
                    break;
                }
            }
            if (!passedFirstTest) {
                System.out.println("Failed Round Robin");
            } else
                System.out.println("Round Robin Test Passed");
            //Check if Unregister really destroyes the Queue and Removes The Service From The QUEUE
            //It shall also remove the microservice from the eventlist/broadcastlist
            //I only check eventlist, you can also check broadcastList
            messageInstance.unregister(hanSoloObj);
            messageInstance.unregister(C3POObj);
            try {
                if (messageInstance.awaitMessage(hanSoloObj) != null)
                    passedSecondTest = false;
                if (messageInstance.awaitMessage(C3POObj) != null)
                    passedSecondTest = false;
            } catch (Exception e) {
            }
            if (!passedSecondTest) System.out.println("Failed Test - Didn't Clear QUEUE Of Object Upon Unregister");
            else System.out.println("Queue Clear Test Passed!");
            Event1 event1 = new Event1();
            Event2 event2 = new Event2();
            Future<Boolean> testEventListen1 = messageInstance.sendEvent(event1);
            Future<Boolean> testEventListen2 = messageInstance.sendEvent(event2);
            if (testEventListen1 != null || testEventListen2 != null)
                System.out.println("Failed EventList Clear Test - Didn't Clear Event List");
            else
                System.out.println("EventList Clear Test Passed");
            /*
             Multi Threaded Check Against SubscribingToEvents
            */

            hanSoloObj = new HanSoloMicroservice();
            messageInstance.register(hanSoloObj);
            messageInstance.subscribeEvent(Event1.class, hanSoloObj);
            messageInstance.subscribeBroadcast(Broadcast1.class
                    , hanSoloObj);
            //Checking If sendEvent Is Synced
            Thread hanSoloSender1 = new Thread(() -> {
                for (int i = 0; i < 500; i++) {
//                    System.out.println("Thread 1 \t event: " + i);
                    messageInstance.sendEvent(new Event1());
                    try {
                        Thread.sleep(5);
                    } catch (Exception e) {
                    }
                }
            });
            Thread hanSoloSender2 = new Thread(() -> {
                for (int i = 0; i < 500; i++) {
//                    System.out.println("Thread 2 \t event: " + i);
                    messageInstance.sendEvent(new Event1());
                    try {
                        Thread.sleep(2);
                    } catch (Exception e) {
                    }
                }
            });
            Thread hanSoloSender3 = new Thread(() -> {
                for (int i = 0; i < 500; i++) {
//                    System.out.println("Thread 3 \t event: " + i);
                    messageInstance.sendEvent(new Event1());
                    try {
                        Thread.sleep(3);
                    } catch (Exception e) {
                    }
                }
            });
            hanSoloSender1.start();
            hanSoloSender2.start();
            hanSoloSender3.start();
            int numMesssagesReceived = 0;
//            Thread.sleep(5500);

            System.out.println("\r\n\r\n------------Initating MultiThreaded SentEvent Test!-------------------");
            System.out.println("IF you get stuck here you you probably do not sync SendEvent");
            Message m;
            do {
//                System.out.println(numMesssagesReceived);
                m = messageInstance.awaitMessage(hanSoloObj);
                if (m != null)
                    numMesssagesReceived++;
                if (numMesssagesReceived >= 1500)
                    break; //If you are not syncing you will most likely get less then 1500
            } while (true);
            System.out.println("Passed Sync Send Event Test! All Is Fine.\r\n----------------------------------");

            System.out.println("\r\nInitating Test Sync By Sabina 1.....\r\n");
            boolean passedTestSabina = true;

            for (int j = 0; passedTestSabina && j < 100; j++) {
                Thread.sleep(10);
                numberOfM3.set(0);
                numberOfM1.set(0);
                numberOfM2.set(0);
                CountDownLatch initialize = new CountDownLatch(3);
                CountDownLatch terminate = new CountDownLatch(3 + j);
                new Thread(new TestMicroServer("M1", initialize, terminate)).start();
                new Thread(new TestMicroServer("M2", initialize, terminate)).start();
                new Thread(new TestMicroServer("M3", initialize, terminate)).start();
                initialize.await();
                CountDownLatch terminateSend = new CountDownLatch(j);
                for (int i = 1; i <= j; i++) {
                    new Thread(new SenderMicroServer("sender", terminateSend, terminate)).start();
                }
                terminateSend.await();
                messageInstance.sendBroadcast(new Broadcast1());
                terminate.await();
                int numberOfEvents = numberOfM1.get() + numberOfM2.get() + numberOfM3.get();
                if (numberOfEvents != j) {
                    System.out.println("Test number " + j + " failed---->" + " only " + numberOfEvents + " was resolved out of " + j);
                    passedTestSabina = false;
                } else {
                    int maxNumberOfEventsPerThread = j / 3 + j % 3;
                    if (numberOfM1.get() > maxNumberOfEventsPerThread || numberOfM2.get() > maxNumberOfEventsPerThread || numberOfM3.get() > maxNumberOfEventsPerThread) {
                        System.out.println("Test number " + j + " failed----> one microServer got more than " + maxNumberOfEventsPerThread + " events problem in round robin");
                        passedTestSabina = false;
                    }
                    System.out.println("Passed Test Number " + j);
                }
            }
            if (passedTestSabina)
                System.out.println("\r\nPassed Sync Test Sabina -> " + passedTestSabina);
            else
                System.out.println("\r\nFailed Sync Test Sabina -> Unregister/Send Broadcast Sync Issue. Make sure they are synced on the same object");
        } catch (Exception logicTestsException) {
            logicTestsException.printStackTrace();
        }
    }
}
