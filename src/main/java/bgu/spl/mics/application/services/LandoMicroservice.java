package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

import java.util.Date;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice extends MicroService {
    private Long duration;

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
    }

    @Override
    protected void initialize() {
        //System.out.println("Lando Initialize");
        subscribeEvent(BombDestroyerEvent.class, event -> {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            complete(event, Boolean.TRUE);

            //System.out.println("Lando Bombs at " + new Date());
        });

        subscribeBroadcast(TerminateBroadcast.class, broadcast -> {
            Diary.getInstance().setLandoTerminate(System.currentTimeMillis());
            terminate();
        });

        //System.out.println("Lando finish initialize at " + new Date());
    }
}
