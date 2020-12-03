package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Attack;

public class AttackEvent implements Event<Boolean> {
    private Attack attack;
    public AttackEvent(Attack attack)
    {
        this.attack = attack;
    }

    public Attack getAttack() {
        return attack;
    }
}
