package bgu.spl.mics.messageHandlers;

import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;


public abstract class MessageHandler {
    protected ArrayList<MicroService> services;

    public MessageHandler() {
        services = new ArrayList<>();
    }

    public void AddHandler(MicroService service) {
        services.add(service);
    }

    public void RemoveHandler(MicroService service) {
        if(services.indexOf(service)!=-1) {
            services.remove(service);
        }
    }

    public abstract void PutMessage(Message msg, ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> servicesQues);
}
