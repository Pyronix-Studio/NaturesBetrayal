package es.pyronixstudio.natures_betrayal.common.entity.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class ShootWebGoal extends Goal {

    private final Mob mob;
    private LivingEntity target;

    private final int shootCooldown = 20 * 10;
    private int actualCooldown = 0;
    public ShootWebGoal(Mob mob){
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        if(target == null)return false;
        double mobDistance = mob.distanceToSqr(target);
        double minDistance = 10 * 10;
        if(mobDistance > minDistance)return false;
        return true;
    }

    @Override
    public void start() {
        this.target = mob.getTarget();
    }

    @Override
    public void stop() {
        this.target = null;
        this.actualCooldown = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if(actualCooldown >= shootCooldown){
            BlockPos pos = target.blockPosition();
            mob.level().setBlock(pos, Blocks.COBWEB.defaultBlockState(), Block.UPDATE_ALL);
            actualCooldown = 0;
            return;
        }

       actualCooldown++;
    }
}
