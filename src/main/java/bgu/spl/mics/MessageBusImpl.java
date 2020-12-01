package bgu.spl.mics;


import bgu.spl.mics.messageHandlers.BroadcastHandler;
import bgu.spl.mics.messageHandlers.EventHandler;
import bgu.spl.mics.messageHandlers.MessageHandler;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

    private static MessageBusImpl instance = null;
    ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> servicesQueues;
    ConcurrentHashMap<Class, MessageHandler> messageHandlers;
    ConcurrentHashMap<Event, Future> eventFutureDictionary;


    private MessageBusImpl() {
        servicesQueues = new ConcurrentHashMap<>();
        messageHandlers = new ConcurrentHashMap<>();
        eventFutureDictionary = new ConcurrentHashMap<>();
    }

    public static MessageBusImpl getInstance() {
        if (instance == null) {
            instance = new MessageBusImpl();
        }
        return instance;
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        if (messageHandlers.get(type) == null) {
            messageHandlers.put(type, new EventHandler());
        }
        messageHandlers.get(type).AddHandler(m);
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        if (messageHandlers.get(type) == null) {
            messageHandlers.put(type, new BroadcastHandler());
        }
        messageHandlers.get(type).AddHandler(m);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void complete(Event<T> e, T result) {
        eventFutureDictionary.get(e).resolve(result);
        eventFutureDictionary.remove(e);
    }

    @Override
    public void sendBroadcast(Broadcast b) {
        messageHandlers.get(b.getClass()).PutMessage(b, servicesQueues);
    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        Future<T> future = new Future<>();
        eventFutureDictionary.put(e, future);

        messageHandlers.get(e.getClass()).PutMessage(e, servicesQueues);

        return future;
    }

    @Override
    public void register(MicroService m) {
        if (servicesQueues.get(m) == null) {
            servicesQueues.put(m, new LinkedBlockingQueue<Message>());
        }
    }

    @Override
    public void unregister(MicroService m) {
        if (servicesQueues.get(m) != null) {
            for (Enumeration<MessageHandler> handlerEnu = messageHandlers.elements(); handlerEnu.hasMoreElements(); ) {
                MessageHandler handler = handlerEnu.nextElement();
                handler.RemoveHandler(m);
            }
            servicesQueues.get(m).notifyAll();
            servicesQueues.remove(m);
        }
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {

        Message msg = servicesQueues.get(m).take();
        return msg;
    }
}
