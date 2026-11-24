package es.pyronixstudio.natures_betrayal.common.mobs.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MobBreakGoal extends Goal {

    private final Mob mob;

    private final Queue<BlockPos> queue = new LinkedList<>();
    private float breakProgress = 0F;
    private BlockPos targetBlock;
    public MobBreakGoal(Mob mob){
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        PathNavigation navigation = mob.getNavigation();
        LivingEntity target = mob.getTarget();
        if(target == null)
            return false;
        if(!mob.getMainHandItem().is(ItemTags.PICKAXES))
            return false;

        if(navigation.isInProgress() || !navigation.isDone())
            return false;

        if(mob.hasLineOfSight(target))
            return false;

        return true;

    }

    @Override
    public void start() {
        mob.getNavigation().stop();
        calculateBlocks();
    }

    @Override
    public void stop() {
      resetBreak();
    }

    private void calculateBlocks() {
        Level level = mob.level();
        LivingEntity target = mob.getTarget();

        Vec3 toTarget = target.position().subtract(mob.position());
        Vec3 horizontal = new Vec3(toTarget.x, -1, toTarget.z).normalize();

        AABB mobBox = mob.getBoundingBox();

        int yExpanded = 1;
        double yDifference = target.getY() - mob.getY();

         if(yDifference < 0)
            yExpanded=-1;

        AABB expanded = mobBox.expandTowards(horizontal.x, yExpanded, horizontal.z);


        BlockCollisions<BlockPos> col = new BlockCollisions<>(
                level, mob, expanded, false,
                (pos, shape) -> pos.immutable()
        );
        while (col.hasNext()) {
            BlockPos pos = col.next();
            if (!mobBox.intersects(AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(pos)))) {
                queue.add(pos);
            }
        }
    }

    private float getBreakSpeed(BlockState state) {
        ItemStack itemInHand = mob.getMainHandItem();

        float speed = itemInHand.getDestroySpeed(state);

        float hardness = state.getDestroySpeed(mob.level(), mob.blockPosition());

        if (hardness < 0) return Float.MAX_VALUE;
        if (hardness == 0) return 1f;


        boolean correctTool = itemInHand.isCorrectToolForDrops(state);
        float divisor = correctTool ? 30f : 100f;


        return (hardness * divisor) / speed;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        Level level = mob.level();
        if(queue.isEmpty() && targetBlock == null)
            return;

        if(targetBlock == null){
            targetBlock = queue.poll();
        }

        BlockState state = level.getBlockState(targetBlock);
        float speed = getBreakSpeed(state);
        breakProgress+=1.0F;

        int stage = (int) ((breakProgress /speed) * 10);

        level.destroyBlockProgress(mob.getId(), targetBlock, Math.min(stage, 9));
        if (breakProgress >= speed) {
            level.destroyBlock(targetBlock, false);
            level.destroyBlockProgress(mob.getId(), targetBlock, -1);
            breakProgress = 0.0F;
            targetBlock = null;
        }

    }

    private void resetBreak(){
        queue.clear();
        breakProgress = 0f;
        if(targetBlock != null){
            mob.level().destroyBlockProgress(mob.getId(), targetBlock, -1);
        }
        targetBlock = null;
    }
}