package bgu.spl.mics.application;

import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.services.R2D2Microservice;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

/**
 * This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
    public static void main(String[] args) {
//        MicroService microService = new R2D2Microservice(123);
//        Dictionary x = new Hashtable();
//        Dictionary<MicroService, LinkedList<Event<String>>> y = new Hashtable<>();
//
//        x.put(microService, new LinkedList<>());
//        ((LinkedList<String>) x.get(microService)).add("Hello");
//        System.out.println((((LinkedList<String>) x.get(microService)).get(0)));
//
////        y.put(microService, new LinkedList<Event> < String >);
////        y.get(microService).add("Bye");
////        System.out.println(y.get(microService).get(0));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("I'm still alive");
            }
        }).start();

        new Thread(() -> {
            System.out.println("Helllo");
        }).start();

        System.out.println("Continue with my life");
    }
}
