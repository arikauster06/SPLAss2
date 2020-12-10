package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Attack;

import java.util.concurrent.atomic.AtomicInteger;

//Coded By Ron Rachev

public class Test {
    private Attack[] attacks;
    int     R2D2;
    int     Lando;
    int     Ewoks;
    int     testId;
    private AtomicInteger numOfAttacks = new AtomicInteger(0);

    public void setEwoks  (int ewoks) {
        Ewoks = ewoks;
    }
    public void setTestId (int testId){
        this.testId = testId;
    }
    public void setLando  (int lando) {
        Lando = lando;
    }
    public void setR2D2   (int r2d2) {
        R2D2 = r2d2;
    }
    public void setAttacks(Attack[] attacks) {
        this.attacks = attacks;
    }
    public int  getR2D2Sleep(){
        return R2D2;
    }
    public AtomicInteger getNumberOfAttacks(){
        return numOfAttacks;
    }

    public void setNumOfAttacks(int value){
        numOfAttacks.set(value);
    }
}
