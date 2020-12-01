package bgu.spl.mics.messageHandlers;

import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;

import java.util.Dictionary;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class BroadcastHandler extends MessageHandler {


    @Override
    public void PutMessage(Message msg, ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> servicesQues) {

        for (MicroService activator : services) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        servicesQues.get(activator).put(msg);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }
}
