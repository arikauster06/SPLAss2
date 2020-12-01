package bgu.spl.mics.messageHandlers;


import bgu.spl.mics.Event;
import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;


public class EventHandler extends MessageHandler {

    private int lastIndex;

    public EventHandler() {
        super();
        lastIndex = -1;
    }

    @Override
    public void RemoveHandler(MicroService service) {
        int index = services.indexOf(service);
        if (index != -1 && index <= lastIndex) {
            lastIndex--;
        }
        super.RemoveHandler(service);

    }

    @Override
    public void PutMessage(Message msg, ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> servicesQues) {
        MicroService activator;
        synchronized (this) {
            int index = lastIndex;
            activator = services.get(index);
            lastIndex = (lastIndex + 1) % services.size();
        }

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
