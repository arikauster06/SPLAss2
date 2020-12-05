package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

import java.util.Date;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {

    private long duration;

    public R2D2Microservice(long duration) {
        super("R2D2");
        this.duration = duration;
    }

    @Override
    protected void initialize() {
        System.out.println("R2D2 Initialize");
        subscribeEvent(DeactivationEvent.class, event -> {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            complete(event, Boolean.TRUE);
            Diary.getInstance().setR2D2Deactivate(System.currentTimeMillis());
        });
        subscribeBroadcast(TerminateBroadcast.class, broadcast -> {
            Diary.getInstance().setR2D2Terminate(System.currentTimeMillis());
            terminate();
        });

        System.out.println("R2D2 finish initialize at "+ new Date());
    }

    public long getDuration() {
        return duration;
    }
}
