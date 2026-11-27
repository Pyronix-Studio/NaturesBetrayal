package es.pyronixstudio.natures_betrayal.forge.events;

import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import es.pyronixstudio.natures_betrayal.common.events.core.PlayerSleepAttemptEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeEntitiesEvents {


    @SubscribeEvent
    public void sleepEvent(PlayerSleepInBedEvent event){
        PlayerSleepAttemptEvent playerSleepAttemptEvent = new PlayerSleepAttemptEvent(event.getEntity(), event.getPos());
        NaturesBetrayal.eventHandler.call(playerSleepAttemptEvent);
        event.setResult(playerSleepAttemptEvent.getBedSleepingProblem());
    }

}
