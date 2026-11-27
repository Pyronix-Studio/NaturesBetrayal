package es.pyronixstudio.natures_betrayal.common.entity.custom_phantom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class CustomPhantom extends FlyingMob {

    @Nullable
    private LivingEntity transportTarget;
    @Nullable
    private BlockPos landingPos;
    @Nullable
    private BlockPos returnPos;

    @Nullable
    private BlockPos pickupPos;
    private float bank;

    @Nullable
    private PhantomRiderGoal riderGoal;
    @Nullable
    private DeliverPassengerGoal deliverGoal;

    public CustomPhantom(EntityType<? extends FlyingMob> type, Level level) {
        super(type, level);
        this.moveControl = new PhantomFlyMoveControl(this);
        this.setNoGravity(true);
        this.setPersistenceRequired();
    }

    public void setTransportTarget(@Nullable LivingEntity target) {
        if (this.transportTarget != target) {
            this.transportTarget = target;
            this.landingPos = null;
            if (deliverGoal != null) {
                deliverGoal.forceRepath = true;
            }
        }
    }

    @Nullable
    public LivingEntity getTransportTarget() {
        return transportTarget;
    }

    @Nullable
    public BlockPos getLandingPos() {
        return landingPos;
    }

    public float getBank() {
        return bank;
    }

    public void setBank(float bank) {
        this.bank = bank;
    }

    public void setRiderGoal(@Nullable PhantomRiderGoal goal) {
        this.riderGoal = goal;
    }

    public void setReturnPos(@Nullable BlockPos pos) {
        this.returnPos = pos;
        this.landingPos = null;
        if (deliverGoal != null) {
            deliverGoal.forceRepath = true;
        }
    }

    public void clearLanding() {
        this.landingPos = null;
        if (deliverGoal != null) {
            deliverGoal.forceRepath = true;
        }
    }

    public void setPickupPos(@Nullable BlockPos pos) {
        this.pickupPos = pos;
    }

    @Nullable
    public BlockPos getPickupPos() {
        return this.pickupPos;
    }


    public boolean isCeilingBlocked() {
        return deliverGoal != null && deliverGoal.ceilingBlocked;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;

        if (riderGoal != null && getFirstPassenger() == null && this.tickCount > 5) {
            riderGoal = null;
            kill();
            return;
        }
        if (riderGoal != null) {
            riderGoal.checkDelivery(this);
            return;
        }

        if (this.tickCount > 5) {
            recoverUnloadedPassenger();
            if (!isAlive()) return;
            if (getFirstPassenger() == null) {
                discard();
            }
        }
    }



    private void recoverUnloadedPassenger() {
        if (returnPos != null) return;
        if (transportTarget != null && transportTarget.isAlive()) return;

        Entity passenger = getFirstPassenger();
        if (!(passenger instanceof Mob mob)) return;
        if (mob.getTarget() != null && mob.getTarget().isAlive()) return;

        BlockPos saved = this.pickupPos;
        if (saved != null) {
            BlockPos nearOrigin = findSafeAround(mob, saved, 2);
            if (nearOrigin != null) {
                dropAt(mob, nearOrigin);
                return;
            }
        }

        BlockPos from = blockPosition();
        BlockPos nearHere = findSafeAround(mob, from, 2);
        if (nearHere != null) {
            dropAt(mob, nearHere);
            return;
        }

        for (int y = from.getY(); y >= level().dimensionType().minY(); y--) {
            BlockPos solid = new BlockPos(from.getX(), y, from.getZ());
            if (!level().getBlockState(solid).isSolid()) continue;

            BlockPos stand = solid.above();
            if (isSafeStand(mob, stand)) {
                dropAt(mob, stand);
                return;
            }
        }
    }

    @Nullable
    private BlockPos findSafeAround(Mob mob, BlockPos center, int radius) {
        // Igual que en la entrega: nunca recolocar al jinete por encima del techo.
        int[] ys = {0, -1, -2, -3};
        for (int r = 0; r <= radius; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != r) continue;
                    for (int dy : ys) {
                        BlockPos stand = center.offset(dx, dy, dz);
                        if (isSafeStand(mob, stand)) return stand;
                    }
                }
            }
        }
        return null;
    }

    private boolean isSafeStand(Mob mob, BlockPos stand) {
        if (!level().getBlockState(stand.below()).isSolid()) return false;
        AABB box = mob.getBoundingBox().move(Vec3.atCenterOf(stand).subtract(mob.position()));
        return level().getBlockStates(box.deflate(0.01D)).noneMatch(BlockState::isSolid);
    }

    private void dropAt(Mob mob, BlockPos stand) {
        mob.stopRiding();
        mob.moveTo(stand.getX() + 0.5D, stand.getY(), stand.getZ() + 0.5D,
                mob.getYRot(), mob.getXRot());
        mob.getNavigation().stop();
        mob.fallDistance = 0.0F;
        this.pickupPos = null;
        discard();
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new PhantomFlightNavigation(this, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return FlyingMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.FLYING_SPEED, 0.6)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }

    @Override
    protected void registerGoals() {
        this.deliverGoal = new DeliverPassengerGoal();
        this.goalSelector.addGoal(1, this.deliverGoal);
    }


    class DeliverPassengerGoal extends Goal {

        private static final int REPATH_COOLDOWN = 20;

        @Nullable
        private BlockPos requestedGoal;
        @Nullable
        private BlockPos activeGoal;
        private int repathTimer;
        private boolean forceRepath;

        private boolean ceilingBlocked;

        @Nullable
        private BlockPos ceilingPos;

        @Override
        public boolean canUse() {
            return returnPos != null || resolveTarget() != null;
        }

        @Override
        public boolean canContinueToUse() {
            return landingPos == null;
        }

        @Override
        public void start() {
            requestedGoal = null;
            activeGoal = null;
            repathTimer = 0;
            forceRepath = false;
            ceilingBlocked = false;
            ceilingPos = null;
        }

        @Override
        public void tick() {
            BlockPos requested = currentRequest();
            if (requested == null) {
                stopFlying();
                return;
            }
            if (ceilingBlocked && returnPos != null) {
                ceilingBlocked = false;
                ceilingPos = null;
                forceRepath = true;
            }

            if (ceilingBlocked) {
                if (ceilingPos != null && !level().getBlockState(ceilingPos).isSolid()) {
                    ceilingBlocked = false;
                    ceilingPos = null;
                    forceRepath = true;
                }
                return;
            }

            PathNavigation nav = getNavigation();
            if (forceRepath || activeGoal == null || !requested.equals(requestedGoal)) {
                forceRepath = false;
                requestedGoal = requested;
                activeGoal = plan(nav, requested);
                repathTimer = REPATH_COOLDOWN;
            }

            Path path = nav.getPath();
            if (activeGoal != null && path != null && path.isDone()) {

                if (nav instanceof PhantomFlightNavigation flight
                        && flight.isNearSafeSurface(activeGoal, blockPosition())) {
                    landingPos = blockPosition();
                    return;
                }
                if (--repathTimer <= 0) {
                    repathTimer = REPATH_COOLDOWN;
                    forceRepath = true;
                }
            } else if (path == null && activeGoal != null) {

                forceRepath = true;
            } else if (activeGoal == null) {
                if (--repathTimer <= 0) {
                    repathTimer = REPATH_COOLDOWN;
                    forceRepath = true;
                }
            }
        }

        @Nullable
        private BlockPos currentRequest() {
            if (returnPos != null) {
                return returnPos;
            }
            LivingEntity current = resolveTarget();
            if (current == null) {
                return null;
            }
            return current.getOnPos().above();
        }

        @Nullable
        private BlockPos plan(PathNavigation nav, BlockPos requested) {
            if (!(nav instanceof PhantomFlightNavigation flight)) {
                Path direct = nav.createPath(requested, 0);
                if (endsAtOrAbove(direct, requested.getY())) {
                    nav.moveTo(direct, 1.0);
                    return requested;
                }
                stopFlying();
                return null;
            }

            int ceiling = flight.firstCeilingBelow(blockPosition(), requested.getY());
            if (ceiling != -1) {
                BlockPos under = flight.standUnderCeiling(blockPosition(), ceiling);
                if (under != null) {
                    Path stopPath = flight.pathTo(Set.of(under));
                    if (endsNearOrAbove(stopPath, under)) {
                        nav.moveTo(stopPath, 1.0);
                        ceilingBlocked = true;
                        ceilingPos = blockPosition().atY(ceiling);
                        return under;
                    }
                }
            }
            ceilingBlocked = false;
            ceilingPos = null;

            BlockPos goal = flight.findLandingTarget(requested);
            Path path = goal == null ? null : flight.pathTo(Set.of(goal));
            BlockPos chosen = goal;

            if (!endsNearOrAbove(path, requested)) {
                BlockPos high = flight.findHighestSafeSurface(requested);
                if (high != null) {
                    Path fallback = flight.pathTo(Set.of(high));
                    if (endsNearOrAbove(fallback, requested)) {
                        path = fallback;
                        chosen = high;
                    }
                }
            }

            if (endsNearOrAbove(path, requested)) {
                nav.moveTo(path, 1.0);
                return chosen;
            }
            stopFlying();
            return null;
        }


        private boolean endsAtOrAbove(Path path, int minY) {
            if (path == null || !path.canReach()) {
                return false;
            }
            Node end = path.getEndNode();
            return end != null && end.y >= minY;
        }


        private boolean endsNearOrAbove(Path path, BlockPos requested) {
            if (path == null) return false;
            Node end = path.getEndNode();
            if (end == null) return false;
            double dx = end.x - requested.getX();
            double dz = end.z - requested.getZ();
            return end.y >= requested.getY() - 1 && dx * dx + dz * dz <= 16.0D;
        }

        private void stopFlying() {
            getNavigation().stop();
            if (moveControl instanceof PhantomFlyMoveControl flight) {
                flight.stop();
            }
            setDeltaMovement(getDeltaMovement().scale(0.5D));
        }

        @Nullable
        private LivingEntity resolveTarget() {
            Entity passenger = getFirstPassenger();
            if (passenger instanceof Mob mob && mob.getTarget() != null && mob.getTarget().isAlive()) {
                return mob.getTarget();
            }
            return transportTarget != null && transportTarget.isAlive() ? transportTarget : null;
        }
    }
}
