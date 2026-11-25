package es.pyronixstudio.natures_betrayal.common.mobs.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.pathfinder.Path;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class SwellGoalNotLineSight extends Goal {
    private final Creeper creeper;
    @Nullable
    private LivingEntity target;

    public SwellGoalNotLineSight(Creeper creeper) {
        this.creeper = creeper;
        this.setFlags(EnumSet.noneOf(Flag.class));

    }

    public boolean canUse() {
        LivingEntity target = this.creeper.getTarget();

        if (target == null || !target.isAlive()) return false;
        if(creeper.hasLineOfSight(target))return false;


        return true;
    }

    public void start() {
        this.target = this.creeper.getTarget();

    }

    public void stop() {
        this.target = null;
        this.creeper.setSwellDir(-1);
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }


    @Override
    public boolean isInterruptable() {
        return true;
    }

    public void tick() {
        if (target == null || !target.isAlive()) {
            this.creeper.setSwellDir(-1);
            return;
        }



        Path path = this.creeper.getNavigation().getPath();
        if (path == null || path.getEndNode() == null) {
            return;
        }
        BlockPos end = path.getEndNode().asBlockPos();
        double endDistance = this.creeper.distanceToSqr(
                end.getX() + 0.5,
                end.getY(),
                end.getZ() + 0.5
        );
        if(endDistance < 2)
            this.creeper.setSwellDir(1);

    }

}
