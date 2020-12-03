package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Diary;
import com.google.gson.Gson;

/**
 * This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
    public static void main(String[] args) {

        Diary.getInstance().initializeAttackersCountdown(2);
        Diary.getInstance().initializeTerminationCountdown(5);

        Gson json = new Gson();


        try {
            Diary.getInstance().TerminationAwait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
