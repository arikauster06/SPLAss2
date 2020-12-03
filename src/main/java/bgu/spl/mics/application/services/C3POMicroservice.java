package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;


/**
 * C3POMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {

    public C3POMicroservice() {
        super("C3PO");
    }

    @Override
    protected void initialize() {
        subscribeEvent(AttackEvent.class, event -> {
//            Ewoks.getInstance().acquire(event.getAttack().getSerials());
//            try {
//                Thread.sleep(event.getAttack().getDuration());
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            Ewoks.getInstance().release(event.getAttack().getSerials());
//            Diary.getInstance().IncrementAttacksAmount();

            AttackUtilis.attackCallback.call(event);

            complete(event, Boolean.TRUE);
            Diary.getInstance().setC3POFinish(System.currentTimeMillis());

        });


        subscribeBroadcast(TerminateBroadcast.class, broadcast -> {
            Diary.getInstance().setC3POTerminate(System.currentTimeMillis());
            terminate();
        });

        Diary.getInstance().AttackerCountdown();
    }
}
