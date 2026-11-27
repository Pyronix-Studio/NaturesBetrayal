package es.pyronixstudio.natures_betrayal.forge.events;

import net.minecraftforge.common.MinecraftForge;

public class ForgeEvents {

    public ForgeEvents(){
        register(new ForgeEntitiesEvents());
    }

    public void register(Object object){
        MinecraftForge.EVENT_BUS.register(object);
    }

}