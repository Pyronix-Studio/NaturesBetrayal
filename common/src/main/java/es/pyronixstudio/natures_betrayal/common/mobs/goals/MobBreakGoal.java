package es.pyronixstudio.natures_betrayal.common.mobs.goals;

import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Iterator;

public class MobBreakGoal extends Goal {

    private final Mob mob;
    public MobBreakGoal(Mob mob){
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        PathNavigation navigation = mob.getNavigation();
        NaturesBetrayal.LOGGER.info(String.format("Progress: %s, Done: %s, Stuck:%s", navigation.isInProgress(), navigation.isDone(), navigation.isStuck()));
        LivingEntity target = mob.getTarget();
        if(target == null)
            return false;

        if(navigation.isInProgress() || !navigation.isDone())
            return false;

        if(mob.hasLineOfSight(target))
            return false;

       // LivingEntity target = mob.getTarget();
        //Vec3 targetPosition = new Vec3(target.getX(), target.getY(), target.getZ());
        //TODO PONER REKERIR PICO
        return true;

    }


    @Override
    public void start() {
        mob.getNavigation().stop();
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        Level level = mob.level();

        Vec3 toTarget = mob.getTarget().position().subtract(mob.position());

// horizontal hacia el target
        Vec3 horizontal = new Vec3(toTarget.x, 0, toTarget.z).normalize();
// vertical solo si hay diferencia significativa de altura
        double verticalOffset = Math.signum(toTarget.y);

        AABB future = mob.getBoundingBox().move(horizontal.x, verticalOffset, horizontal.z);

        BlockCollisions<BlockPos> col = new BlockCollisions<>(
                level, mob, future, false,
                (pos, shape) -> pos.immutable()
        );

        while (col.hasNext()) {
            BlockPos pos = col.next();
            level.destroyBlock(pos, false);
        }

    }
}