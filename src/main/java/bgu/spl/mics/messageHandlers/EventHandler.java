package bgu.spl.mics.messageHandlers;


import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class EventHandler implements MessageHandler {

    private ConcurrentLinkedQueue<MicroService> serives;

    public EventHandler() {
        serives = new ConcurrentLinkedQueue();
    }

    @Override
    public void AddHandler(MicroService service) {
        serives.add(service);
    }

    @Override
    public void RemoveHandler(MicroService service) {
        serives.remove(service);
    }

    @Override
    public void PutMessage(Message msg, ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> servicesQues) {
        MicroService activator;
        synchronized (this) {
            activator = serives.poll();
            AddHandler(activator);
        }

        try {
            servicesQues.get(activator).put(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Only Happens if the MicroService called unregistered, and the messageBus deleted it from the mao
        // In that case the queue is not exists anymore, and further more, the microservice is not handing
        // events nor broadcast anymore
        catch (NullPointerException e) {
            System.out.println("MicroService is has unregistered");
        }

    }

    @Override
    public boolean hasMicroservice(MicroService microService) {
        return serives.contains(microService);
    }
}
