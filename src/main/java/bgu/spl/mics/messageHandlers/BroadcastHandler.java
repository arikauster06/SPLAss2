package bgu.spl.mics.messageHandlers;

import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class BroadcastHandler implements MessageHandler {

    private ArrayList<MicroService> services;

    public BroadcastHandler() {
        services = new ArrayList<>();
    }

    public synchronized void AddHandler(MicroService service) {
        services.add(service);
    }

    public synchronized void RemoveHandler(MicroService service) {
        if (services.indexOf(service) != -1) {
            services.remove(service);
        }
    }

    @Override
    public void PutMessage(Message msg, ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> servicesQues) {
        LinkedList<MicroService> copied;
        synchronized (this) {
            copied = new LinkedList<>(services);
        }

        for (MicroService activator : copied) {
            System.out.println("Sending " + msg + "\t To " + activator);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        servicesQues.get(activator).put(msg);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Only Happens if the MicroService called unregistered, and the messageBus deleted it from the mao
                    // In that case the queue is not exists anymore, and further more, the microservice is not handing
                    // events nor broadcast anymore
                    catch (NullPointerException e) {
                        System.out.println("MicroService has unregistered");
                    }
                }
            }).start();

        }
    }

    @Override
    public boolean hasMicroservice(MicroService microService) {
        return services.contains(microService);
    }
}
