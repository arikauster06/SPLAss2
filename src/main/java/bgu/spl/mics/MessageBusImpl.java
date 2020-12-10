package bgu.spl.mics;


import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

    private static class MessageBusHolder {
        private static MessageBusImpl instance = new MessageBusImpl();
    }

    private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> servicesQueues;
    private ConcurrentHashMap<Class<? extends Message>, LinkedBlockingQueue<MicroService>> messageHandlers;
    private ConcurrentHashMap<Event, Future> eventFutureDictionary;


    private MessageBusImpl() {
        servicesQueues = new ConcurrentHashMap<>();
        eventFutureDictionary = new ConcurrentHashMap<>();
        messageHandlers = new ConcurrentHashMap<>();
    }

    public static MessageBusImpl getInstance() {
        return MessageBusHolder.instance;
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        synchronized (type) {
            if (messageHandlers.get(type) == null) {
                messageHandlers.put(type, new LinkedBlockingQueue<>());
            }
            messageHandlers.get(type).add(m);
        }
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        synchronized (type) {
            if (messageHandlers.get(type) == null) {
                messageHandlers.put(type, new LinkedBlockingQueue<>());
            }
            messageHandlers.get(type).add(m);
        }
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
//        new Thread(() ->
//        {
//            if (messageHandlers.get(b.getClass()) != null)
//                messageHandlers.get(b.getClass()).PutMessage(b, servicesQueues);
//        }).start();
        // in case that there is no one how can handle this type of broadcast we ignore it
        if (messageHandlers.get(b.getClass()) != null &&
                !messageHandlers.get(b.getClass()).isEmpty()) {
            for (MicroService activator : messageHandlers.get(b.getClass())) {
                //System.out.println("Sending " + msg + "\t To " + activator);
                try {
                    servicesQueues.get(activator).put(b);
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
        }
    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        // in case that there is no one how can handle this type of event we ignore the event
        if (messageHandlers.get(e.getClass()) == null ||
                messageHandlers.get(e.getClass()).isEmpty()) {
            return null;
        }

        Future<T> future = new Future<>();
        eventFutureDictionary.put(e, future);
//        synchronized (e.getClass()) {

        MicroService activator;
        boolean isSent = false;
        do {
            synchronized (e.getClass()) {
                activator = messageHandlers.get(e.getClass()).peek();
                messageHandlers.get(e.getClass()).add(activator);
                messageHandlers.get(e.getClass()).poll();

            }

            try {
                isSent = servicesQueues.get(activator).offer(e);
            }
            // Only Happens if the MicroService called unregistered, and the messageBus deleted it from the mao
            // In that case the queue is not exists anymore, and further more, the microservice is not handing
            // events nor broadcast anymore
            catch (NullPointerException error) {
                System.out.println("MicroService has unregistered");
            }
        }
        while (!isSent);
//            messageHandlers.get(e.getClass()).PutMessage(e, servicesQueues);

        return future;

    }

    @Override
    public void register(MicroService m) {
        // In here we aren't worried about synchronization, because register happens
        // before any other action which related to "initialization part", which happens in the same thread
        if (servicesQueues.get(m) == null) {
            //System.out.println(m.name + " registers at " + new Date());
            servicesQueues.put(m, new LinkedBlockingQueue<Message>());
            //System.out.println(servicesQueues);
        }
    }

    @Override
    public void unregister(MicroService m) {
        if (servicesQueues.get(m) != null) {
            // Removing the service from all the events and broadcast handlers
            for (Enumeration<LinkedBlockingQueue<MicroService>> handlerEnu = messageHandlers.elements(); handlerEnu.hasMoreElements(); ) {
                LinkedBlockingQueue que = handlerEnu.nextElement();
                // Unregister can only preformed once.
                // Moreover the only place where a microservice is being removed from an handler is ,
                // here
                // therefore we can call this async methods
                if (que.contains(m)) {
                    que.remove(m);
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
            //while (servicesQueues.get((m)) == null) ;
            msg = servicesQueues.get(m).take();
        } catch (NullPointerException e) {
            //e.printStackTrace();
        }
        return msg;
    }
}
