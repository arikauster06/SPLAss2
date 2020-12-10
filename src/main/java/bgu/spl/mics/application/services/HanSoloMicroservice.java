package bgu.spl.mics.application.services;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

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
        subscribeEvent(AttackEvent.class, event -> {
            AttackUtilis.attackCallback.call(event);
            Diary.getInstance().setHanSoloFinish(System.currentTimeMillis());
            complete(event, Boolean.TRUE);
        });
        subscribeBroadcast(TerminateBroadcast.class, broadcast -> {
            Diary.getInstance().setHanSoloTerminate(System.currentTimeMillis());
            terminate();
        });
        Diary.getInstance().AttackerCountdown();
    }
}
