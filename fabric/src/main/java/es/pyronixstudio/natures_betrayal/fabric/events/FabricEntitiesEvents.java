package es.pyronixstudio.natures_betrayal.fabric.events;

import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import es.pyronixstudio.natures_betrayal.common.events.PlayerSleepAttemptEvent;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public final class FabricEntitiesEvents {


   public static void register(){
      EntitySleepEvents.ALLOW_SLEEPING.register(FabricEntitiesEvents::allowSleeping);
   }

   private static Player.@Nullable BedSleepingProblem allowSleeping(Player player, BlockPos blockPos) {

      PlayerSleepAttemptEvent playerSleepAttemptEvent = new PlayerSleepAttemptEvent(player, blockPos);
      NaturesBetrayal.eventHandler.call(playerSleepAttemptEvent);
      return playerSleepAttemptEvent.getBedSleepingProblem();

   }


}
