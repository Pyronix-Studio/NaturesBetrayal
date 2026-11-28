package es.pyronixstudio.natures_betrayal.common.events;

import es.pyronixstudio.natures_betrayal.common.config.NaturesBetrayalConfig;
import es.pyronixstudio.util.events.EventListener;
import es.pyronixstudio.util.events.Subscribe;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class EntityEvents implements EventListener {


    @Subscribe
    public void sleepEvent(PlayerSleepAttemptEvent event){
        if(NaturesBetrayalConfig.CAN_SLEEP.get())return;
        Player player = event.getPlayer();
        player.displayClientMessage(Component.translatable("message.natures_betrayal.player.can_not_sleep").withStyle(ChatFormatting.RED), true);
        event.setBedSleepingProblem(Player.BedSleepingProblem.OTHER_PROBLEM);

    }

}
