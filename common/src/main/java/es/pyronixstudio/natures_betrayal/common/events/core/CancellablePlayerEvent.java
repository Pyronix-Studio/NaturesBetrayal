package es.pyronixstudio.natures_betrayal.common.events.core;

import es.pyronixstudio.util.events.CancellableEvent;
import net.minecraft.world.entity.player.Player;

public class CancellablePlayerEvent extends CancellableEvent {

    public final Player player;

    public CancellablePlayerEvent(Player player) {
        this.player = player;
    }
}
