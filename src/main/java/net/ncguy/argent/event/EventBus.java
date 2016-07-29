package net.ncguy.argent.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Guy on 27/07/2016.
 */
public class EventBus {

    private class EventBusException extends RuntimeException {
        public EventBusException(String message) { super(message); }
    }

    private List<Object> subscribers;

    public EventBus() {
        this.subscribers = new LinkedList<>();
    }

    public void register(Object subscriber) {
        subscribers.add(subscriber);
    }

    public void unregister(Object subscriber) {
        subscribers.remove(subscriber);
    }

    public void post(Object event) {
        try{
            final Class eventType = event.getClass();
            for(Object subscriber : subscribers) {
                for(Method method : subscriber.getClass().getDeclaredMethods()) {
                    if(isSubscriber(method)) {
                        if(method.getParameterTypes().length != 1) {
                            throw new EventBusException(String.format("Size of parameter list of method %s in %s must be 1", method.getName(), subscriber.getClass().getName()));
                        }
                        if(method.getParameterTypes()[0].equals(eventType))
                            method.invoke(subscriber, eventType.cast(event));
                    }
                }
            }
        }catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private boolean isSubscriber(Method method) {
        boolean isSub = method.isAnnotationPresent(Subscribe.class);
        if(isSub) return true;

        Class[] interfaces = method.getDeclaringClass().getInterfaces();
        for(Class i : interfaces) {
            try{
                Method interfaceMethod = i.getMethod(method.getName(), method.getParameterTypes());
                if(interfaceMethod != null) {
                    isSub = interfaceMethod.isAnnotationPresent(Subscribe.class);
                    if(isSub) return true;
                }
            }catch (NoSuchMethodException nsme) {

            }
        }
        return false;
    }

}
