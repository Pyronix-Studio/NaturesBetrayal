package es.pyronixstudio.natures_betrayal.common.entity.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Queue;

public class MobBreakGoal extends Goal {

    private final Mob mob;


    private final Queue<BlockPos> queue = new LinkedList<>();

    private LivingEntity cachedTarget;
    private Direction cachedDirection;
    private BlockPos targetBlock;
    private float breakProgress = 0F;

    public MobBreakGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive())
            return false;
        if (!mob.getMainHandItem().is(ItemTags.PICKAXES))
            return false;

        PathNavigation navigation = mob.getNavigation();
        if (navigation.isInProgress())
            return false;

        if (GoalPaths.canReachTarget(mob, target))
            return false;

        double yDiff = target.getY() - mob.getY();
        double dx = target.getX() - mob.getX();
        double dz = target.getZ() - mob.getZ();
        double horizontal = Math.sqrt(dx * dx + dz * dz);

        if (yDiff <= 0.5 && yDiff >= -1.5 && horizontal < 2.5 && mob.hasLineOfSight(target)) {
            return false;
        }


        Direction direction = GoalDirections.toward(mob, target);
        Queue<BlockPos> blocks = findBlockingBlocks(direction, target);

        if (blocks.isEmpty())
            return false;

        cachedTarget = target;
        cachedDirection = direction;
        queue.clear();
        queue.addAll(blocks);
        return true;
    }

    @Override
    public void start() {
        mob.getNavigation().stop();
        breakProgress = 0F;
        targetBlock = null;
        mob.getLookControl().setLookAt(cachedTarget, 30f, 30f);
    }

    @Override
    public boolean canContinueToUse() {
        if (cachedTarget == null || !cachedTarget.isAlive() || mob.getTarget() != cachedTarget)
            return false;

        return targetBlock != null || !queue.isEmpty();
    }

    @Override
    public void stop() {

        if (cachedTarget != null && cachedTarget.isAlive()) {
            boolean diggingDown = cachedDirection == Direction.DOWN;

            if (!diggingDown) {
                mob.getNavigation().moveTo(cachedTarget, 1.0);
            }
        }
        resetBreak();
        cachedTarget = null;
    }

    private Queue<BlockPos> findBlockingBlocks(Direction direction, LivingEntity target) {
        Queue<BlockPos> result = new LinkedList<>();


        AABB body = mob.getBoundingBox();
        if (mob.getVehicle() != null) {
            body = body.minmax(mob.getVehicle().getBoundingBox());
        }

        Entity vehicle = mob.getVehicle();
        if (vehicle != null) {
            for (BlockPos self : new BlockPos[] {
                    mob.blockPosition(), vehicle.blockPosition()
            }) {
                if (isBreakable(self)
                        && body.intersects(AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(self)))) {
                    if (!result.contains(self)) result.add(self);
                }
            }
            if (!result.isEmpty()) return result;
        }

        for (BlockPos origin : new BlockPos[] {
                mob.blockPosition(),
                mob.getVehicle() != null ? mob.getVehicle().blockPosition() : mob.blockPosition()
        }) {
            if (direction == Direction.UP) {
                if (isBreakable(origin.above())) result.add(origin.above());
                if (isBreakable(origin.above(2))) result.add(origin.above(2));
            } else if (direction == Direction.DOWN) {
                if (isBreakable(origin.below())) result.add(origin.below());
            }
        }
        if (direction == Direction.UP || direction == Direction.DOWN) {
            return result;
        }

        Vec3 step = new Vec3(direction.getStepX(), 0, direction.getStepZ());
        AABB nextBody = body.move(step);

        BlockCollisions<BlockPos> collisions = new BlockCollisions<>(
                mob.level(), mob, nextBody, false,
                (pos, shape) -> pos.immutable()
        );
        while (collisions.hasNext()) {
            BlockPos pos = collisions.next();
            if (body.intersects(AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(pos))))
                continue;
            if (!result.contains(pos) && isBreakable(pos)) {
                result.add(pos);
            }
        }
        return result;
    }

    private boolean isBreakable(BlockPos pos) {
        BlockState state = mob.level().getBlockState(pos);
        return !state.isAir() && state.getDestroySpeed(mob.level(), pos) >= 0;
    }

    private float getBreakSpeed(BlockState state, BlockPos pos) {
        ItemStack itemInHand = mob.getMainHandItem();

        float speed = itemInHand.getDestroySpeed(state);
        if (speed <= 0f)
            return Float.MAX_VALUE;

        float hardness = state.getDestroySpeed(mob.level(), pos);

        if (hardness < 0) return Float.MAX_VALUE;
        if (hardness == 0) return 1f;

        //boolean correctTool = itemInHand.isCorrectToolForDrops(state);
        //float divisor = correctTool ? 30f : 100f;
        float divisor = 30.0F;
        return (hardness * divisor) / speed;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        Level level = mob.level();
        if (cachedTarget == null)
            return;

        mob.getLookControl().setLookAt(cachedTarget, 30f, 30f);

        if (targetBlock == null) {
            targetBlock = queue.poll();
        }
        if (targetBlock == null)
            return;

        BlockState state = level.getBlockState(targetBlock);
        float speed = getBreakSpeed(state, targetBlock);
        if (state.isAir() || speed >= Float.MAX_VALUE || speed <= 0f) {
            level.destroyBlockProgress(mob.getId(), targetBlock, -1);
            targetBlock = null;
            return;
        }

        breakProgress += 1.0F;

        int stage = (int) ((breakProgress / speed) * 10);
        level.destroyBlockProgress(mob.getId(), targetBlock, Math.min(stage, 9));
        if (breakProgress >= speed) {
            level.destroyBlock(targetBlock, false);
            level.destroyBlockProgress(mob.getId(), targetBlock, -1);
            breakProgress = 0.0F;
            targetBlock = null;
        }
    }

    private void resetBreak() {
        queue.clear();
        breakProgress = 0f;
        if (targetBlock != null) {
            mob.level().destroyBlockProgress(mob.getId(), targetBlock, -1);
        }
        targetBlock = null;
    }
}
