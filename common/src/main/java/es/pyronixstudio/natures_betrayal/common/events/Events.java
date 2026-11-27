package es.pyronixstudio.natures_betrayal.common.events;

import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import es.pyronixstudio.util.events.EventListener;

import java.util.function.Supplier;

public final class Events {

    public static void start(){
        register(EntityEvents::new);
    }

    private static void register(EventListener eventListener){
        NaturesBetrayal.eventHandler.register(eventListener);
    }

    private static void register(Supplier<EventListener> eventListener){
        register(eventListener.get());
    }

}
