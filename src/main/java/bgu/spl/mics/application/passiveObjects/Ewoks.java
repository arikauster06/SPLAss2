package bgu.spl.mics.application.passiveObjects;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {
    private static Ewoks instance = null;
    private LinkedList<Ewok> ewoksList;

    private Ewoks(int numberOfEwoks) {
        ewoksList = new LinkedList<>();
        ewoksList.addFirst(null);
        for (int i = 1; i <= numberOfEwoks; i++) {
            ewoksList.addLast(new Ewok(i));
        }
    }

    public static void initialize(int numberOfEwoks) {
        if (instance == null) {
            instance = new Ewoks(numberOfEwoks);
        }
    }

    public static Ewoks getInstance() {
        if (instance == null) {
            throw new RuntimeException("Ewoks was not initialized yet");
        }
        return instance;
    }

    public boolean acquire(List<Integer> ewoks) {
        ewoks.sort(Integer::compareTo);
        for (int i = 0; i < ewoks.size(); i++) {
            ewoksList.get(ewoks.get(i)).acquire();
        }
        return true;
    }

    public boolean release(List<Integer> ewoks) {
        ewoks.sort(Integer::compareTo);
        for (Integer ewokId : ewoks) {
            ewoksList.get(ewokId).release();
        }
        return true;
    }

}
