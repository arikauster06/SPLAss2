package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a forest creature summoned when HanSolo and C3PO receive AttackEvents.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Ewok {
    int serialNumber;
    boolean available;

    public Ewok(int serialNumber) {
        this.serialNumber = serialNumber;
        available = true;
    }

    public synchronized boolean isAvailable() {
        return this.available;
    }

    public int getSerialNumber() {
        return this.serialNumber;
    }

    /**
     * Acquires an Ewok
     */
    public synchronized void acquire() {
        while (!this.available) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.available = false;
    }

    /**
     * release an Ewok
     */
    public synchronized void release() {
        if (!isAvailable()) {
            available = true;
            notifyAll();
        }
    }
}
