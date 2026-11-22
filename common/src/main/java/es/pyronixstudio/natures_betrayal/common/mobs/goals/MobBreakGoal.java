
package es.pyronixstudio.natures_betrayal.common.mobs.goals;

import java.util.EnumSet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MobBreakGoal extends Goal {

    private static final double SEARCH_DISTANCE = 1.0D;

    private final Mob mob;

    private BlockPos blockPos;

    private int breakTime;
    private int maxBreakTime;
    private int lastBreakProgress = -1;

    public MobBreakGoal(Mob mob) {
        this.mob = mob;

        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!mob.getMainHandItem().is(ItemTags.PICKAXES)) {
            return false;
        }

        LivingEntity target = mob.getTarget();

        if (target == null || !target.isAlive()) {
            return false;
        }

        // Let normal navigation handle reachable targets.
        Path path = mob.getNavigation().createPath(target, 0);

        if (path != null && path.canReach()) {
            return false;
        }

        blockPos = findObstacle();

        return blockPos != null;
    }

    /**
     * Finds the closest block obstructing the mob's movement
     * towards its target.
     */
    private BlockPos findObstacle() {
        LivingEntity target = mob.getTarget();

        if (target == null || !target.isAlive()) {
            return null;
        }

        Vec3 difference = target.position().subtract(mob.position());

        if (difference.lengthSqr() < 0.0001D) {
            return null;
        }

        Direction direction = Direction.getNearest(
                (float) difference.x,
                (float) difference.y,
                (float) difference.z
        );

        Vec3 movement = new Vec3(
                direction.getStepX(),
                direction.getStepY(),
                direction.getStepZ()
        ).scale(SEARCH_DISTANCE);

        AABB futureBox = mob.getBoundingBox().move(movement);

        Level level = mob.level();

        int minX = Mth.floor(futureBox.minX);
        int minY = Mth.floor(futureBox.minY);
        int minZ = Mth.floor(futureBox.minZ);

        int maxX = Mth.floor(futureBox.maxX);
        int maxY = Mth.floor(futureBox.maxY);
        int maxZ = Mth.floor(futureBox.maxZ);

        BlockPos closestBlock = null;
        double closestDistance = Double.MAX_VALUE;

        for (BlockPos pos : BlockPos.betweenClosed(
                minX, minY, minZ,
                maxX, maxY, maxZ
        )) {
            BlockState state = level.getBlockState(pos);

            if (state.isAir()) {
                continue;
            }

            float hardness = state.getDestroySpeed(level, pos);

            if (hardness < 0.0F) {
                continue;
            }

            VoxelShape shape = state.getCollisionShape(level, pos);

            if (shape.isEmpty()) {
                continue;
            }

            if (!intersects(futureBox, shape, pos)) {
                continue;
            }

            double distance = mob.distanceToSqr(
                    Vec3.atCenterOf(pos)
            );

            if (distance < closestDistance) {
                closestDistance = distance;
                closestBlock = pos.immutable();
            }
        }

        return closestBlock;
    }

    /**
     * Checks whether a block's collision shape intersects
     * the specified bounding box.
     */
    private boolean intersects(AABB box, VoxelShape shape, BlockPos pos) {
        for (AABB collisionBox : shape.toAabbs()) {
            AABB worldBox = collisionBox.move(
                    pos.getX(),
                    pos.getY(),
                    pos.getZ()
            );

            if (box.intersects(worldBox)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void start() {
        breakTime = 0;
        lastBreakProgress = -1;

        calculateBreakTime();

        mob.getNavigation().stop();
    }

    private void calculateBreakTime() {
        Level level = mob.level();
        BlockState state = level.getBlockState(blockPos);
        ItemStack tool = mob.getMainHandItem();

        float hardness = state.getDestroySpeed(level, blockPos);
        float speed = tool.getDestroySpeed(state);

        if (hardness <= 0.0F) {
            maxBreakTime = 1;
            return;
        }

        if (speed <= 0.0F) {
            maxBreakTime = Integer.MAX_VALUE;
            return;
        }

        int divisor = tool.isCorrectToolForDrops(state) ? 30 : 100;

        maxBreakTime = Math.max(
                1,
                Mth.ceil(hardness * divisor / speed)
        );
    }

    @Override
    public boolean canContinueToUse() {
        return blockPos != null
                && breakTime <= maxBreakTime
                && mob.getTarget() != null
                && mob.getTarget().isAlive()
                && mob.getMainHandItem().is(ItemTags.PICKAXES)
                && isStillObstacle();
    }

    private boolean isStillObstacle() {
        if (blockPos == null) {
            return false;
        }

        Level level = mob.level();

        BlockState state = level.getBlockState(blockPos);

        if (state.isAir() || state.getDestroySpeed(level, blockPos) < 0.0F) {
            return false;
        }

        LivingEntity target = mob.getTarget();

        if (target == null || !target.isAlive()) {
            return false;
        }

        Vec3 difference = target.position().subtract(mob.position());

        if (difference.lengthSqr() < 0.0001D) {
            return false;
        }

        Direction direction = Direction.getNearest(
                (float) difference.x,
                (float) difference.y,
                (float) difference.z
        );

        Vec3 movement = new Vec3(
                direction.getStepX(),
                direction.getStepY(),
                direction.getStepZ()
        ).scale(SEARCH_DISTANCE);

        AABB futureBox = mob.getBoundingBox().move(movement);

        VoxelShape shape = state.getCollisionShape(level, blockPos);

        return intersects(futureBox, shape, blockPos);
    }

    @Override
    public void tick() {
        if (blockPos == null) {
            return;
        }

        mob.getLookControl().setLookAt(
                Vec3.atCenterOf(blockPos)
        );

        if (mob.getRandom().nextInt(20) == 0) {
            mob.swing(InteractionHand.MAIN_HAND);
        }

        ++breakTime;

        int progress = Mth.clamp(
                (int) ((float) breakTime / maxBreakTime * 10.0F),
                0,
                9
        );

        if (progress != lastBreakProgress) {
            mob.level().destroyBlockProgress(
                    mob.getId(),
                    blockPos,
                    progress
            );

            lastBreakProgress = progress;
        }

        if (breakTime >= maxBreakTime) {
            mob.level().destroyBlockProgress(
                    mob.getId(),
                    blockPos,
                    -1
            );

            mob.level().destroyBlock(
                    blockPos,
                    true,
                    mob
            );

            blockPos = null;
        }
    }

    @Override
    public void stop() {
        if (blockPos != null) {
            mob.level().destroyBlockProgress(
                    mob.getId(),
                    blockPos,
                    -1
            );
        }

        blockPos = null;
    }
}
