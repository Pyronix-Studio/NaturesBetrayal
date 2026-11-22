package es.pyronixstudio.natures_betrayal.common.mobs.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;

public class ZombieBridgeGoal extends Goal {

    private static final int PLACE_COOLDOWN = 12;
    private static final int MAX_HEIGHT_DIFF = 8;
    private static final double HORIZONTAL_RANGE = 16.0;

    private enum Phase { VERTICAL, HORIZONTAL }

    private final Mob mob;
    private int cooldown = 0;
    private LivingEntity cachedTarget = null;
    private Phase phase = Phase.VERTICAL;

    public ZombieBridgeGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    // ── Condiciones ───────────────────────────────────────────────────────────

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        if (target == null) return false;

        int vertDiff = target.blockPosition().getY() - mob.blockPosition().getY();
        double horizDist = horizontalDistanceTo(target);

        // Nunca si el target está más abajo
        if (vertDiff < 0) return false;
        if (horizDist > HORIZONTAL_RANGE) return false;
        PathNavigation navigation = mob.getNavigation();
        if (navigation.isInProgress() || !navigation.isDone()) return false;

        boolean needsVertical   = vertDiff >= 2 && vertDiff <= MAX_HEIGHT_DIFF;
        boolean needsHorizontal = vertDiff <= 1 && horizDist > 2.5;

        if (!needsVertical && !needsHorizontal) return false;

        cachedTarget = target;
        phase = needsVertical ? Phase.VERTICAL : Phase.HORIZONTAL;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (cachedTarget == null || !cachedTarget.isAlive()) return false;
        if (horizontalDistanceTo(cachedTarget) > HORIZONTAL_RANGE) return false;

        int vertDiff = cachedTarget.blockPosition().getY() - mob.blockPosition().getY();

        // Si el target baja durante la construcción, para
        if (vertDiff < 0) return false;

        return switch (phase) {
            case VERTICAL   -> vertDiff > 0;
            case HORIZONTAL -> horizontalDistanceTo(cachedTarget) > 1.5;
        };
    }

    @Override
    public void start() {
        cooldown = 0;
    }

    @Override
    public void stop() {
        // Devuelve el control al pathfinder apuntando al objetivo
        if (cachedTarget != null) {
            mob.getNavigation().moveTo(cachedTarget, 1.0);
        }
        cachedTarget = null;
        cooldown = 0;
    }

    // ── Lógica principal ──────────────────────────────────────────────────────

    @Override
    public void tick() {
        if (cachedTarget == null) return;

        mob.getLookControl().setLookAt(cachedTarget, 30f, 30f);

        int vertDiff = cachedTarget.blockPosition().getY() - mob.blockPosition().getY();

        // Si el target baja, para
        if (vertDiff < 0) return;

        // Transición vertical → horizontal cuando ya está al mismo nivel
        if (phase == Phase.VERTICAL && vertDiff <= 0) {
            phase = Phase.HORIZONTAL;
            cooldown = 0;
        }

        if (cooldown > 0) {
            cooldown--;
            if (phase == Phase.VERTICAL) mob.getNavigation().stop();
            return;
        }

        switch (phase) {
            case VERTICAL   -> tickVertical();
            case HORIZONTAL -> tickHorizontal();
        }
    }

    // ── Fase vertical ─────────────────────────────────────────────────────────

    private void tickVertical() {
        mob.getNavigation().stop();

        BlockPos feet   = mob.blockPosition();
        BlockPos ground = feet.below();
        BlockPos above  = feet.above();
        BlockPos head   = feet.above(2);

        if (!isSolid(ground)) return;
        if (isSolid(above) || isSolid(head)) return;

        if (canPlace(feet)) {
            placeBlock(feet);
            mob.jumpFromGround();
            mob.swing(InteractionHand.MAIN_HAND);
            cooldown = PLACE_COOLDOWN;
        }
    }

    // ── Fase horizontal ───────────────────────────────────────────────────────

    private void tickHorizontal() {
        BlockPos feet  = mob.blockPosition();
        BlockPos below = feet.below();

        // Abismo bajo los pies → pone soporte primero
        if (isAirOrVoid(below)) {
            if (canPlace(below)) {
                placeBlock(below);
                mob.swing(InteractionHand.MAIN_HAND);
                cooldown = PLACE_COOLDOWN;
            }
            return;
        }

        // Pone bloque en el suelo del próximo paso si no hay suelo
        Direction facing   = getFacingToTarget();
        BlockPos nextFloor = below.relative(facing);
        BlockPos nextFeet  = feet.relative(facing);

        boolean nextHasFloor = isSolid(nextFloor);
        boolean nextIsClear  = !isSolid(nextFeet) && !isSolid(nextFeet.above());

        if (!nextHasFloor && nextIsClear) {
            if (canPlace(nextFloor)) {
                placeBlock(nextFloor);
                mob.swing(InteractionHand.MAIN_HAND);
                cooldown = PLACE_COOLDOWN;
            }
        }

        // Mueve hacia el objetivo manteniendo la Y actual
        mob.getMoveControl().setWantedPosition(
                cachedTarget.getX(),
                mob.getY(),
                cachedTarget.getZ(),
                1.0
        );
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Direction getFacingToTarget() {
        double dx = cachedTarget.getX() - mob.getX();
        double dz = cachedTarget.getZ() - mob.getZ();
        if (Math.abs(dx) >= Math.abs(dz)) {
            return dx > 0 ? Direction.EAST : Direction.WEST;
        } else {
            return dz > 0 ? Direction.SOUTH : Direction.NORTH;
        }
    }

    private boolean isSolid(BlockPos pos) {
        BlockState state = mob.level().getBlockState(pos);
        return !state.isAir() && state.isSolidRender(mob.level(), pos);
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
    }

    private double horizontalDistanceTo(LivingEntity entity) {
        double dx = entity.getX() - mob.getX();
        double dz = entity.getZ() - mob.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }
}