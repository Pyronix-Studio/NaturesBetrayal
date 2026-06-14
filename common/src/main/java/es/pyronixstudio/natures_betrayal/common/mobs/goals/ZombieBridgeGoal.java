package es.pyronixstudio.natures_betrayal.common.mobs.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;

import java.util.EnumSet;

public class ZombieBridgeGoal extends Goal {
    private final Mob mob;
    private int cooldown = 0;

    public ZombieBridgeGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        if (target == null) {
            return false;
        }

        int verticalDiff = target.blockPosition().getY() - mob.blockPosition().getY();
        if (verticalDiff <= 1) return false;

        boolean needBuild = mob.getNavigation().isDone() && !mob.getNavigation().isInProgress();
        if(!needBuild)
            return false;

        double dx = target.getX() - mob.getX();
        double dz = target.getZ() - mob.getZ();
        double horizontalDist = Math.sqrt(dx * dx + dz * dz);

        if (horizontalDist > 3.0) return false;


        return true;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = mob.getTarget();
        if (target == null) return false;
        return target.blockPosition().getY() - mob.blockPosition().getY() > 0;
    }

    @Override
    public void tick() {
        if (cooldown > 0) {
            cooldown--;
            return;
        }
        BlockPos mobPos = mob.blockPosition();
        BlockPos above = mob.blockPosition().above();
        BlockPos below = mob.blockPosition().below();
        if(mob.level().getBlockState(below).isAir())
            return;

        if (mob.level().getBlockState(above).isAir()) {
            mob.jumpFromGround();
            mob.swing(InteractionHand.MAIN_HAND);
            mob.level().setBlockAndUpdate(mobPos, Blocks.DIRT.defaultBlockState());
            cooldown = 10; // medio segundo entre bloques
        }
    }
}
