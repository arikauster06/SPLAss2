package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.messages.AttackEvent;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
    private Attack[] attacks;
    private Future<Boolean>[] futures;

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
        this.attacks = attacks;
        this.futures = new Future[attacks.length];
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class, c -> {
            Diary.getInstance().setLeiaTerminate(System.currentTimeMillis());
            terminate();});

        try {
            Diary.getInstance().AttackersAwait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < attacks.length; i++) {
            AttackEvent attackEvent = new AttackEvent(attacks[i]);
            futures[i] = sendEvent(attackEvent);
        }
        for (Future future : futures) {
            future.get();
        }
        Future<Boolean> R2D2Future = sendEvent(new DeactivationEvent());
        R2D2Future.get();
        Future<Boolean> LandoFuture = sendEvent(new BombDestroyerEvent());
        LandoFuture.get();

        sendBroadcast(new TerminateBroadcast());
        // Attacks

    }
}
