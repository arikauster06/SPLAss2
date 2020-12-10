package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.AppInputData;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
    public static void main(String[] args) {

        LeiaMicroservice Leia;
        HanSoloMicroservice HanSolo;
        C3POMicroservice C3PO;
        R2D2Microservice R2D2;
        LandoMicroservice Lando;



        Diary.getInstance().initializeAttackersCountdown(2);
        Diary.getInstance().initializeTerminationCountdown(5);

        Gson gson = new Gson();
        try {
//            Reader reader = Files.newBufferedReader(Paths.get(args[0]));
            Reader reader = Files.newBufferedReader(Paths.get("input.json"));
            AppInputData inputData = gson.fromJson(reader, AppInputData.class);

            Ewoks.initialize(inputData.getEwoks());

            Leia = new LeiaMicroservice(inputData.getAttacks());
            HanSolo = new HanSoloMicroservice();
            C3PO = new C3POMicroservice();
            R2D2 = new R2D2Microservice(inputData.getR2D2());
            Lando = new LandoMicroservice(inputData.getLando());

            Thread LeiaThread = new Thread(Leia, "Leia-Thread");
            Thread HanSoloThread = new Thread(HanSolo, "HanSolo-Thread");
            Thread C3POTThread = new Thread(C3PO, "C3PO-Thread");
            Thread R2D2Thread = new Thread(R2D2, "R2D2-Thread");
            Thread LandoThread = new Thread(Lando, "Lando-Thread");

            LeiaThread.start();
            HanSoloThread.start();
            C3POTThread.start();
            R2D2Thread.start();
            LandoThread.start();


            // Terminating
            Diary.getInstance().TerminationAwait();
            //Thread.sleep(500);
            System.out.println("Leia alive - " + LeiaThread.isAlive());
            System.out.println("Han Solo alive - " + HanSoloThread.isAlive());
            System.out.println("C3PO alive - " + C3POTThread.isAlive());
            System.out.println("R2D2 alive - " + R2D2Thread.isAlive());
            System.out.println("Lando alive - " + LandoThread.isAlive());

            System.out.println("Terminated");


            Map<String, Object> outputMap = new HashMap<>();

            outputMap.put("totalAttacks", Diary.getInstance().getTotalAttacks());
            outputMap.put("HanSoloFinish", Diary.getInstance().getHanSoloFinish());
            outputMap.put("C3POFinish", Diary.getInstance().getC3POFinish());
            outputMap.put("R2D2Deactivate", Diary.getInstance().getR2D2Deactivate());
            outputMap.put("LeiaTerminate", Diary.getInstance().getLeiaTerminate());
            outputMap.put("HanSoloTerminate", Diary.getInstance().getHanSoloTerminate());
            outputMap.put("C3POTerminate", Diary.getInstance().getC3POTerminate());
            outputMap.put("R2D2Terminate", Diary.getInstance().getR2D2Terminate());
            outputMap.put("LandoTerminate", Diary.getInstance().getLandoTerminate());

            Gson outputGson = new Gson();
//            Writer writer = new FileWriter(args[1]);
            Writer writer = new FileWriter("output.json");
            outputGson.toJson(outputMap, writer);
            writer.close();


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
