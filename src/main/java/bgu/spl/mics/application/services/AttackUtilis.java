package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

public class AttackUtilis {

    public static Callback<AttackEvent> attackCallback =  new Callback<AttackEvent>() {
        @Override
        public void call(AttackEvent c) {
            Ewoks.getInstance().acquire(c.getAttack().getSerials());
            try {
                Thread.sleep(c.getAttack().getDuration());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Ewoks.getInstance().release(c.getAttack().getSerials());
            Diary.getInstance().IncrementAttacksAmount();
        }
    };
}
