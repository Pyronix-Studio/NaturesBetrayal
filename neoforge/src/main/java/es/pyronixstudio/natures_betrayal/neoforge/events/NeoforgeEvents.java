package es.pyronixstudio.natures_betrayal.neoforge.events;

import net.neoforged.neoforge.common.NeoForge;

public class NeoforgeEvents {

    public NeoforgeEvents(){
        register(new NeoforgeEntitiesEvents());
    }

    public void register(Object object){
        NeoForge.EVENT_BUS.register(object);
    }

}