package es.pyronixstudio.natures_betrayal.neoforge.events;

import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import es.pyronixstudio.natures_betrayal.common.events.core.PlayerSleepAttemptEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;

public class NeoforgeEntitiesEvents {


    @SubscribeEvent
    public void sleepEvent(CanPlayerSleepEvent event){
        PlayerSleepAttemptEvent playerSleepAttemptEvent = new PlayerSleepAttemptEvent(event.getEntity(), event.getPos());
        NaturesBetrayal.eventHandler.call(playerSleepAttemptEvent);
        event.setProblem(playerSleepAttemptEvent.getBedSleepingProblem());
    }

}
