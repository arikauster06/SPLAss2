package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.messages.AttackEvent;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Diary;

import java.util.Date;

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

        //System.out.println("Leia Initilize at " + new Date());
        subscribeBroadcast(TerminateBroadcast.class, c -> {
            Diary.getInstance().setLeiaTerminate(System.currentTimeMillis());
            terminate();
        });

        try {
            //System.out.println("Liea waits for attackers to finish initialize");
            Diary.getInstance().AttackersAwait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //System.out.println("Leia starts sending attacks at " + new Date());
        for (int i = 0; i < attacks.length; i++) {
            AttackEvent attackEvent = new AttackEvent(attacks[i]);
            futures[i] = sendEvent(attackEvent);
        }

        //System.out.println("Leia waits for all attacks to end at " + new Date());
        for (Future future : futures) {
            future.get();
        }
        //System.out.println("Leia sends Deactviation event to R2D2");
        Future<Boolean> R2D2Future = sendEvent(new DeactivationEvent());
        //System.out.println("Liea waits for R2D2 to finish");
        R2D2Future.get();
        //System.out.println("Liea sends Bomb event to Lando at " + new Date());
        Future<Boolean> LandoFuture = sendEvent(new BombDestroyerEvent());

        //System.out.println("Leia waits for Lando to finish at " + new Date());
        LandoFuture.get();

        //System.out.println("Leia sends Terminate broadcast at " + new Date());
        sendBroadcast(new TerminateBroadcast());
        //System.out.println("Leia finish initialize at "+ new Date());

    }
}
