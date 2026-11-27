package es.pyronixstudio.natures_betrayal.common.entity.custom_phantom;

import es.pyronixstudio.natures_betrayal.common.registries.Registries;
import es.pyronixstudio.natures_betrayal.common.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PhantomRiderGoal extends Goal {

    private static final double TRIGGER_Y_DIFF = 20.0;
    private static final int COOLDOWN_TICKS = 120;
    private static final int RETURN_TIMEOUT_TICKS = 40 * 20;

    private static final double ARRIVAL_HORIZONTAL_SQ = 25.0;
    private static final double ARRIVAL_VERTICAL = 3.0;

    private final Mob rider;
    private CustomPhantom phantom;
    private LivingEntity target;
    private boolean delivered;
    private boolean returning;
    private long returnDeadline;
    private long nextUseTick;

    @Nullable
    private BlockPos committedDrop;

    @Nullable
    private BlockPos origin;
    private float originYRot;
    private float originXRot;

    public PhantomRiderGoal(Mob rider) {
        this.rider = rider;
    }

    @Override
    public boolean canUse() {
        if (rider.isPassenger()) return false;
        if (rider.level().getGameTime() < nextUseTick) return false;
        if (!(rider.level() instanceof ServerLevel)) return false;
        if (!rider.getNavigation().isDone()) return false;

        target = rider.getTarget();
        if (target == null || !target.isAlive()) return false;
        return Math.abs(target.getY() - rider.getY()) > TRIGGER_Y_DIFF;
    }

    @Override
    public boolean canContinueToUse() {
        return !delivered && phantom != null && phantom.isAlive();
    }

    @Override
    public void start() {
        delivered = false;
        returning = false;
        returnDeadline = 0L;
        committedDrop = null;
        target = rider.getTarget();
        if (target == null || !(rider.level() instanceof ServerLevel serverLevel)) {
            delivered = true;
            return;
        }

        origin = rider.getOnPos().above();
        originYRot = rider.getYRot();
        originXRot = rider.getXRot();

        phantom = Registries.ENTITY_TYPES.CUSTOM_PHANTOM.get()
                .create(serverLevel);
        if (phantom == null) {
            delivered = true;
            return;
        }

        phantom.setPickupPos(origin);
        phantom.setTransportTarget(target);
        phantom.moveTo(rider.getX(), rider.getY() + 0.5, rider.getZ(), rider.getYRot(), 0.0F);
        serverLevel.addFreshEntity(phantom);

        rider.getNavigation().stop();
        if (!rider.startRiding(phantom, true)) {
            phantom.discard();
            phantom = null;
            delivered = true;
            return;
        }
        phantom.setRiderGoal(this);
    }

    @Override
    public void tick() {
        if (delivered || phantom == null) return;
        LivingEntity sensed = rider.getTarget();
        if (sensed != null && sensed.isAlive()) {
            if (returning) {
                cancelReturn(phantom);
            }
            if (target != sensed) {
                committedDrop = null;
            }
            target = sensed;
            phantom.setTransportTarget(sensed);
        } else if (!returning) {
            beginReturn(phantom);
            return;
        }

        checkDelivery(phantom);
    }

    public void checkDelivery(CustomPhantom carrier) {
        if (delivered || carrier == null || !carrier.isAlive()) return;
        if (this.phantom != null && this.phantom != carrier) return;

        if (!rider.isAlive()) {
            carrier.setRiderGoal(null);
            carrier.kill();
            phantom = null;
            delivered = true;
            return;
        }
        if (carrier.getFirstPassenger() != rider) return;

        LivingEntity sensed = rider.getTarget();
        if (sensed != null && sensed.isAlive()) {
            if (returning) {
                cancelReturn(carrier);
            }
            if (target != sensed) {
                committedDrop = null;
            }
            target = sensed;
            carrier.setTransportTarget(sensed);
        } else if (!returning) {
            beginReturn(carrier);
            return;
        }

        Mob passenger = carrier.getFirstPassenger() instanceof Mob m ? m : null;
        if (passenger == null) return;

        if (returning && carrier.level().getGameTime() >= returnDeadline) {
            if (forceDropNear(carrier)) return;
            returnDeadline = carrier.level().getGameTime() + RETURN_TIMEOUT_TICKS;
            return;
        }

        BlockPos stand = null;
        if (committedDrop != null && isSafeStand(carrier, passenger, committedDrop)
                && isNearCommitted(committedDrop, carrier)) {
            stand = committedDrop;
        } else {
            if (committedDrop != null) {
                committedDrop = null;
            }

            BlockPos destination;
            if (returning && origin != null && isSafeStand(carrier, passenger, origin)) {
                destination = origin;
            } else if (!returning && target != null && target.isAlive()) {
                destination = target.getOnPos().above();
            } else {
                destination = carrier.blockPosition();
            }

            double dx = carrier.getX() - (destination.getX() + 0.5D);
            double dz = carrier.getZ() - (destination.getZ() + 0.5D);
            double dy = carrier.getY() - (destination.getY() + 0.5D);
            boolean inArrivalZone = dx * dx + dz * dz <= ARRIVAL_HORIZONTAL_SQ
                    && Math.abs(dy) <= ARRIVAL_VERTICAL;
            if (!inArrivalZone) {
                return;
            }

            stand = findSafeStand(carrier, passenger);
            if (stand == null) {
                if (!carrier.isCeilingBlocked()) {
                    carrier.clearLanding();
                }
                return;
            }
            committedDrop = stand;
        }

        deliverAt(carrier, stand);
    }

    private boolean forceDropNear(CustomPhantom carrier) {
        if (!(carrier.getFirstPassenger() instanceof Mob)) return false;
        if (!carrier.dropPassengerAtNearestSafeBlock()) return false;

        carrier.setRiderGoal(null);
        phantom = null;
        delivered = true;
        returning = false;
        returnDeadline = 0L;
        target = null;
        committedDrop = null;
        origin = null;
        nextUseTick = rider.level().getGameTime() + COOLDOWN_TICKS;
        return true;
    }

    private boolean isNearCommitted(BlockPos stand, CustomPhantom carrier) {
        double dx = carrier.getX() - (stand.getX() + 0.5D);
        double dz = carrier.getZ() - (stand.getZ() + 0.5D);
        double dy = carrier.getY() - (stand.getY() + 0.5D);
        return dx * dx + dz * dz <= 4.0D && dy >= -0.5D && dy <= 3.5D;
    }

    private BlockPos findSafeStand(CustomPhantom carrier, Mob passenger) {
        BlockPos here = carrier.blockPosition();
        int[] ys = {0, -1, -2, -3};
        for (int radius = 0; radius <= 1; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) continue;
                    for (int dy : ys) {
                        BlockPos stand = here.offset(dx, dy, dz);
                        if (isSafeStand(carrier, passenger, stand)) return stand;
                    }
                }
            }
        }
        return null;
    }

    private boolean isSafeStand(CustomPhantom carrier, Mob passenger, BlockPos stand) {
        if (!BlockUtils.isSolid(carrier.level(), stand.below())) return false;
        AABB box = passenger.getBoundingBox().move(Vec3.atCenterOf(stand).subtract(passenger.position()));
        return BlockPos.betweenClosedStream(box.deflate(0.01D))
                .noneMatch(pos -> BlockUtils.isSolid(carrier.level(), pos));
    }

    private void beginReturn(CustomPhantom carrier) {
        if (returning) return;
        returning = true;
        returnDeadline = carrier.level().getGameTime() + RETURN_TIMEOUT_TICKS;
        target = null;
        committedDrop = null;
        carrier.setTransportTarget(null);
        carrier.setReturnPos(origin != null ? origin : rider.getOnPos().above());
    }

    private void cancelReturn(CustomPhantom carrier) {
        returning = false;
        returnDeadline = 0L;
        committedDrop = null;
        carrier.setReturnPos(null);
    }

    private void deliverAt(CustomPhantom carrier, BlockPos stand) {
        float yRot = returning && origin != null ? originYRot : rider.getYRot();
        float xRot = returning && origin != null ? originXRot : rider.getXRot();
        rider.stopRiding();

        rider.moveTo(stand.getX() + 0.5D, stand.getY(), stand.getZ() + 0.5D, yRot, xRot);
        rider.getNavigation().stop();
        rider.fallDistance = 0.0F;

        carrier.setRiderGoal(null);
        carrier.discard();
        this.phantom = null;
        delivered = true;
        returning = false;
        returnDeadline = 0L;
        target = null;
        committedDrop = null;
        origin = null;
        carrier.setPickupPos(null);
        nextUseTick = rider.level().getGameTime() + COOLDOWN_TICKS;
    }

    @Override
    public void stop() {
        if (!delivered && phantom != null) {
            if (rider.getVehicle() == phantom) {
                rider.stopRiding();
            }
            phantom.setRiderGoal(null);
            phantom.setPickupPos(null);
            phantom.discard();
            phantom = null;
        }
        target = null;
        origin = null;
        committedDrop = null;
        returning = false;
        returnDeadline = 0L;
    }
}
