package es.pyronixstudio.natures_betrayal.common.events.core;


import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class PlayerSleepAttemptEvent extends PlayerEvent {

    private Player.BedSleepingProblem bedSleepingProblem;
    private final BlockPos bedPos;

    public PlayerSleepAttemptEvent(Player player, BlockPos bedPos) {
        super(player);
        this.bedPos = bedPos;
    }

    public Player.BedSleepingProblem getBedSleepingProblem() {
        return bedSleepingProblem;
    }

    public void setBedSleepingProblem(Player.BedSleepingProblem bedSleepingProblem) {
        this.bedSleepingProblem = bedSleepingProblem;
    }

    public BlockPos getBedPos() {
        return bedPos;
    }
}