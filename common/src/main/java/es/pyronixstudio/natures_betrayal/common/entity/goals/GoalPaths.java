package es.pyronixstudio.natures_betrayal.common.entity.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.pathfinder.Path;

public final class GoalPaths {

    private GoalPaths() {
    }

    public static boolean isMounted(Mob mob) {
        return mob.isPassenger();
    }

    public static boolean canReachTarget(Mob mob, LivingEntity target) {
        Path path = mob.getNavigation().getPath();

        return path != null && path.canReach()
                && path.getTarget().distSqr(target.blockPosition()) <= 4;
    }
}
