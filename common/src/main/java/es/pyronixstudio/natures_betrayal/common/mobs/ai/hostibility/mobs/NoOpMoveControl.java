package es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.mobs;

// NoOpMoveControl.java

import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.Mob;

public class NoOpMoveControl extends MoveControl {
    public NoOpMoveControl(Mob mob) {
        super(mob);
    }

    @Override
    public void tick() {
        // No hace nada — la AI sigue viva pero no puede mover al phantom
    }
}