package bgu.spl.mics;


import bgu.spl.mics.messageHandlers.BroadcastHandler;
import bgu.spl.mics.messageHandlers.EventHandler;
import bgu.spl.mics.messageHandlers.MessageHandler;

import java.util.Date;
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
            synchronized (MessageBusImpl.class) {
                if (instance == null) {
                    instance = new MessageBusImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        if (messageHandlers.get(type) == null) {
            synchronized (type) {
                if (messageHandlers.get(type) == null) {
                    messageHandlers.put(type, new EventHandler());
                }
            }
        }
        messageHandlers.get(type).AddHandler(m);
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        if (messageHandlers.get(type) == null) {
            synchronized (type) {
                if (messageHandlers.get(type) == null) {
                    messageHandlers.put(type, new BroadcastHandler());
                }
            }
        }
        messageHandlers.get(type).AddHandler(m);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void complete(Event<T> e, T result) {
        synchronized (e) {
            if (eventFutureDictionary.get(e) != null) {
                eventFutureDictionary.get(e).resolve(result);
                eventFutureDictionary.remove(e);
            }
        }
    }

    @Override
    public void sendBroadcast(Broadcast b) {
        new Thread(() ->
        {
            if (messageHandlers.get(b.getClass()) != null)
                messageHandlers.get(b.getClass()).PutMessage(b, servicesQueues);
        }).start();
    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        Future<T> future = new Future<>();

        new Thread(() -> {
            eventFutureDictionary.put(e, future);
            messageHandlers.get(e.getClass()).PutMessage(e, servicesQueues);
        }).start();


        return future;
    }

    @Override
    public void register(MicroService m) {
        // In here we aren't worried about synchronization, because register happens
        // before any other action which related to "initialization part", which happens in the same thread
        if (servicesQueues.get(m) == null) {
            //System.out.println(m.name + " registers at " + new Date());
            servicesQueues.put(m, new LinkedBlockingQueue<Message>());
            System.out.println(servicesQueues);
        }
    }

    @Override
    public void unregister(MicroService m) {
        if (servicesQueues.get(m) != null) {
            // Removing the service from all the events and broadcast handlers
            for (Enumeration<MessageHandler> handlerEnu = messageHandlers.elements(); handlerEnu.hasMoreElements(); ) {
                MessageHandler handler = handlerEnu.nextElement();
                // Unregister can only preformed once.
                // Moreover the only place where a microservice is being removed from an handler is ,
                // here
                // therefore we can call this async methods
                if (handler.hasMicroservice(m)) {
                    handler.RemoveHandler(m);
                }
            }
            // finally removing its' queue from the map, indicating it won't be used anymore
            servicesQueues.remove(m);
        }
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {

        Message msg = null;
        try {
            msg = servicesQueues.get(m).take();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return msg;
    }
}
