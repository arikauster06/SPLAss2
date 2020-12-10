package bgu.spl.mics.application.services;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

import java.util.Date;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {

    public HanSoloMicroservice() {
        super("Han");
    }


    @Override
    protected void initialize() {
        //System.out.println("han Solo Initialize");
        subscribeEvent(AttackEvent.class, event -> {

//            Ewoks.getInstance().acquire(event.getAttack().getSerials());
//            try {
//                Thread.sleep(event.getAttack().getDuration());
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            Ewoks.getInstance().release(event.getAttack().getSerials());
//
//            Diary.getInstance().IncrementAttacksAmount();

            //System.out.println("Han solo attacks with Ewoks: "+ event.getAttack().getSerials().toString() + " Attack Duretion:"+ event.getAttack().getDuration()+" At : " + new Date());
            AttackUtilis.attackCallback.call(event);
            Diary.getInstance().setHanSoloFinish(System.currentTimeMillis());
            //System.out.println("Han Solo finished its' attack");
            complete(event, Boolean.TRUE);


        });
        subscribeBroadcast(TerminateBroadcast.class, broadcast -> {
            Diary.getInstance().setHanSoloTerminate(System.currentTimeMillis());
            terminate();
        });
        //System.out.println("hanSolo finish initialize at " + new Date());
        Diary.getInstance().AttackerCountdown();

    }
}
