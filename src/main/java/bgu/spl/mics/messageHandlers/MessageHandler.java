package bgu.spl.mics.messageHandlers;

import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;


public interface MessageHandler {

    void AddHandler(MicroService service);

    void RemoveHandler(MicroService service);

    void PutMessage(Message msg, ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> servicesQues);

    boolean hasMicroservice(MicroService microService);
}
