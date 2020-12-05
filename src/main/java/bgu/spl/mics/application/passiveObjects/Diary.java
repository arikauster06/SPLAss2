package bgu.spl.mics.application.passiveObjects;


import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary {

    private static Diary instance = null;

    private AtomicInteger totalAttacks;
    private long HanSoloFinish;
    private long C3POFinish;
    private long R2D2Deactivate;
    private long LeiaTerminate;
    private long HanSoloTerminate;
    private long C3POTerminate;
    private long R2D2Terminate;
    private long LandoTerminate;

    private CountDownLatch AttackersLatch;
    private Object AttackersLatchLocker = new Object();

    private CountDownLatch TerminationLatch;
    private Object TerminationLocker = new Object();

    private Diary() {
        totalAttacks = new AtomicInteger(0);
        HanSoloFinish = 0;
        C3POFinish = 0;
        R2D2Deactivate = 0;
        LeiaTerminate = 0;
        HanSoloTerminate = 0;
        C3POTerminate = 0;
        R2D2Terminate = 0;
        LandoTerminate = 0;
    }

    public static Diary getInstance() {
        if (instance == null) {
            instance = new Diary();
        }
        return instance;
    }

    public long getHanSoloFinish() {
        return HanSoloFinish;
    }

    public void setHanSoloFinish(long hanSoloFinish) {
        HanSoloFinish = hanSoloFinish;
    }

    public long getC3POFinish() {
        return C3POFinish;
    }

    public void setC3POFinish(long c3POFinish) {
        C3POFinish = c3POFinish;
    }

    public long getR2D2Deactivate() {
        return R2D2Deactivate;
    }

    public void setR2D2Deactivate(long r2D2Deactivate) {
        System.out.println("R2D2 Deactivates at " + new Date());
        R2D2Deactivate = r2D2Deactivate;
    }

    public long getLeiaTerminate() {
        return LeiaTerminate;
    }

    public void setLeiaTerminate(long leiaTerminate) {
        LeiaTerminate = leiaTerminate;
        TerminationCountdown();
    }

    public long getHanSoloTerminate() {
        return HanSoloTerminate;
    }

    public void setHanSoloTerminate(long hanSoloTerminate) {
        HanSoloTerminate = hanSoloTerminate;
        TerminationCountdown();
    }

    public long getC3POTerminate() {
        return C3POTerminate;
    }

    public void setC3POTerminate(long c3POTerminate) {
        C3POTerminate = c3POTerminate;
        TerminationCountdown();
    }

    public long getR2D2Terminate() {
        return R2D2Terminate;
    }

    public void setR2D2Terminate(long r2D2Terminate) {
        R2D2Terminate = r2D2Terminate;
        TerminationCountdown();
    }

    public long getLandoTerminate() {
        return LandoTerminate;
    }

    public void setLandoTerminate(long landoTerminate) {
        LandoTerminate = landoTerminate;
        TerminationCountdown();
    }

    public void initializeAttackersCountdown(int countdown) {
        AttackersLatch = new CountDownLatch(countdown);
    }

    public void AttackerCountdown() {
        synchronized (AttackersLatchLocker) {
            AttackersLatch.countDown();
        }
    }

    public void AttackersAwait() throws InterruptedException {
        AttackersLatch.await();
    }

    public void initializeTerminationCountdown(int countdown) {
        TerminationLatch = new CountDownLatch(countdown);
    }

    public void TerminationCountdown() {
        synchronized (TerminationLocker) {
            TerminationLatch.countDown();
        }
    }

    public void TerminationAwait() throws InterruptedException {
        TerminationLatch.await();
    }


    public void IncrementAttacksAmount() {
        int current;
        do {
            current = totalAttacks.get();
        }
        while (!totalAttacks.compareAndSet(current, current + 1));
    }

    public AtomicInteger getTotalAttacks() {
        return totalAttacks;
    }
}
