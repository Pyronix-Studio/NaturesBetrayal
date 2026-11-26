package es.pyronixstudio.natures_betrayal.common.mobs.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;

public class ZombieBridgeGoal extends Goal {

    private static final int PLACE_COOLDOWN = 12;
    private static final int MAX_HEIGHT_DIFF = 19;
    private static final double HORIZONTAL_RANGE = 16.0;

    private static final double LEVEL_EPSILON = 0.1;

    private static final double MIN_START_HEIGHT = 5.0;

    private static final double MIN_RESUME_HEIGHT = 1.0;

    private enum Phase { VERTICAL, HORIZONTAL }

    private final Mob mob;
    private int cooldown = 0;

    private LivingEntity interruptedTarget = null;
    private LivingEntity cachedTarget = null;
    private Phase phase = Phase.VERTICAL;

    public ZombieBridgeGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }


    @Override
    public boolean canUse() {

        if (GoalPaths.isMounted(mob))
            return false;
        LivingEntity target = mob.getTarget();
        if (target == null) return false;


        int levelDiff = target.blockPosition().getY() - mob.blockPosition().getY();
        double yDiff = verticalDistanceTo(target);
        double horizDist = horizontalDistanceTo(target);

        boolean resuming = interruptedTarget == target;

        if (yDiff < -LEVEL_EPSILON) return false;
        if (horizDist > HORIZONTAL_RANGE) return false;


        if (GoalPaths.canReachTarget(mob, target))
            return false;


        double requiredHeight = resuming ? MIN_RESUME_HEIGHT : MIN_START_HEIGHT;
        boolean needsVertical   = yDiff >= requiredHeight
                && levelDiff <= MAX_HEIGHT_DIFF;

        boolean needsHorizontal = yDiff <= LEVEL_EPSILON && isGapTowards(target);

        if (!needsVertical && !needsHorizontal) return false;

        cachedTarget = target;
        phase = needsVertical ? Phase.VERTICAL : Phase.HORIZONTAL;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (GoalPaths.isMounted(mob)) return false;
        if (cachedTarget == null || !cachedTarget.isAlive()) return false;
        if (horizontalDistanceTo(cachedTarget) > HORIZONTAL_RANGE) return false;

        double yDiff = verticalDistanceTo(cachedTarget);

        if (yDiff < -LEVEL_EPSILON) return false;

        return switch (phase) {
            case VERTICAL   -> yDiff > LEVEL_EPSILON;
            case HORIZONTAL -> targetAbove(cachedTarget) || isGapTowards(cachedTarget);
        };
    }

    @Override
    public void start() {
        mob.getNavigation().stop();
        cooldown = 0;
        interruptedTarget = null;
    }

    @Override
    public void stop() {
        if (cachedTarget != null) {
            mob.getNavigation().moveTo(cachedTarget, 1.0);
        }

        if (cachedTarget != null
                && verticalDistanceTo(cachedTarget) > LEVEL_EPSILON) {
            interruptedTarget = cachedTarget;
        }
        cachedTarget = null;
        cooldown = 0;
    }


    @Override
    public void tick() {
        if (cachedTarget == null) return;

        mob.getLookControl().setLookAt(cachedTarget, 30f, 30f);

        double yDiff = verticalDistanceTo(cachedTarget);

        if (yDiff < -LEVEL_EPSILON) return;

        if (phase == Phase.VERTICAL && yDiff <= LEVEL_EPSILON) {
            phase = Phase.HORIZONTAL;
            cooldown = 0;
            interruptedTarget = null;
        } else if (phase == Phase.HORIZONTAL && targetAbove(cachedTarget)) {
            phase = Phase.VERTICAL;
            interruptedTarget = cachedTarget;
            cooldown = 0;
        }

        if (cooldown > 0) {
            cooldown--;
            mob.getNavigation().stop();
            return;
        }

        switch (phase) {
            case VERTICAL   -> tickVertical();
            case HORIZONTAL -> tickHorizontal();
        }
    }


    private void tickVertical() {
        mob.getNavigation().stop();

        BlockPos feet   = mob.blockPosition();
        BlockPos ground = feet.below();
        BlockPos above  = feet.above();
        BlockPos head   = feet.above(2);

        if (!isSolid(ground)) return;
        if (isSolid(above) || isSolid(head)) return;

        double centerX = feet.getX() + 0.5;
        double centerZ = feet.getZ() + 0.5;
        if (Math.abs(mob.getX() - centerX) > 0.001 || Math.abs(mob.getZ() - centerZ) > 0.001) {
            mob.setPos(centerX, mob.getY(), centerZ);
            mob.setDeltaMovement(0.0, mob.getDeltaMovement().y(), 0.0);
        }

        if (canPlace(feet)) {
            placeBlock(feet);
            mob.swing(InteractionHand.MAIN_HAND);
        }

        mob.jumpFromGround();
        cooldown = PLACE_COOLDOWN;
    }

    private void tickHorizontal() {
        BlockPos feet  = mob.blockPosition();
        BlockPos below = feet.below();

        if (isAirOrVoid(below)) {
            if (!isSolid(below.below()) && canPlace(below)) {
                placeBlock(below);
                mob.swing(InteractionHand.MAIN_HAND);
                cooldown = PLACE_COOLDOWN;
            }
            return;
        }

        Direction facing   = getFacingToTarget();
        BlockPos nextFloor = below.relative(facing);
        BlockPos nextFeet  = feet.relative(facing);

        boolean nextHasFloor = isSolid(nextFloor);
        boolean nextIsClear  = !isSolid(nextFeet) && !isSolid(nextFeet.above());
        boolean placed = false;

        if (!nextHasFloor && nextIsClear && canPlace(nextFloor)) {
            placeBlock(nextFloor);
            placed = true;
        }

        if (nextHasFloor && GoalDirections.isDiagonal(mob, cachedTarget)) {
            Direction side = GoalDirections.secondary(mob, cachedTarget);
            BlockPos cornerFloor = nextFloor.relative(side);
            BlockPos cornerFeet = cornerFloor.above();
            if (!isSolid(cornerFloor)
                    && !isSolid(cornerFeet) && !isSolid(cornerFeet.above())
                    && canPlace(cornerFloor)) {
                placeBlock(cornerFloor);
                placed = true;
            }
        }

        if (placed) {
            mob.swing(InteractionHand.MAIN_HAND);
            cooldown = PLACE_COOLDOWN;
        }

        mob.getMoveControl().setWantedPosition(
                cachedTarget.getX(),
                mob.getY(),
                cachedTarget.getZ(),
                1.0
        );
    }


    private Direction getFacingToTarget() {
        return GoalDirections.primary(mob, cachedTarget);
    }

    private boolean isGapTowards(LivingEntity target) {
        BlockPos below = mob.blockPosition().below();
        Direction facing = GoalDirections.primary(mob, target);

        if (isGap(below.relative(facing))) return true;

        if (GoalDirections.isDiagonal(mob, target)) {
            Direction side = GoalDirections.secondary(mob, target);
            if (isGap(below.relative(facing).relative(side))) return true;
        }
        return false;
    }

    private boolean isGap(BlockPos floor) {
        return isAirOrVoid(floor) && !isSolid(floor.below());
    }

    private boolean isSolid(BlockPos pos) {
        BlockState state = mob.level().getBlockState(pos);
        return !state.canBeReplaced() && !state.getCollisionShape(mob.level(), pos).isEmpty();
    }

    private boolean isAirOrVoid(BlockPos pos) {
        return mob.level().getBlockState(pos).isAir();
    }

    private boolean canPlace(BlockPos pos) {
        BlockState current = mob.level().getBlockState(pos);
        return current.isAir() || current.canBeReplaced();
    }

    private void placeBlock(BlockPos pos) {
        mob.level().setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());

        mob.level().playSound(
                null, pos,
                SoundType.GRAVEL.getPlaceSound(),
                SoundSource.BLOCKS, 1.0F, 1.0F
        );
    }

    private double horizontalDistanceTo(LivingEntity entity) {
        double dx = entity.getX() - mob.getX();
        double dz = entity.getZ() - mob.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    private double verticalDistanceTo(LivingEntity entity) {
        return entity.getY() - mob.getY();
    }

    private boolean targetAbove(LivingEntity target) {
        return target.blockPosition().getY() > mob.blockPosition().getY()
                || verticalDistanceTo(target) >= MIN_RESUME_HEIGHT - LEVEL_EPSILON;
    }
}