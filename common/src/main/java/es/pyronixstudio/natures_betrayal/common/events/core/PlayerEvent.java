package es.pyronixstudio.natures_betrayal.common.events.core;

import es.pyronixstudio.util.events.Event;
import net.minecraft.world.entity.player.Player;

public class PlayerEvent extends Event {

    private final Player player;

    public PlayerEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

}
