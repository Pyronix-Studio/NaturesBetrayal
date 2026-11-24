package es.pyronixstudio.natures_betrayal.common.mobs.entity.phantom;

import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import org.jetbrains.annotations.Nullable;

public class PhantomRiderGoal extends Goal {

    private static final double TRIGGER_Y_DIFF = 20.0;
    private static final int COOLDOWN_TICKS = 120;

    private final Mob rider;
    private CustomPhantom phantom;
    private LivingEntity target;
    private boolean delivered;
    private boolean returning;
    private long nextUseTick;

    /** Donde estaba el mob antes de montarse, para devolverlo si se pierde el objetivo. */
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
        target = rider.getTarget();
        if (target == null || !(rider.level() instanceof ServerLevel serverLevel)) {
            delivered = true;
            return;
        }

        origin = rider.getOnPos().above();
        originYRot = rider.getYRot();
        originXRot = rider.getXRot();

        phantom = NaturesBetrayal.registries.ENTITY_TYPES.CUSTOM_PHANTOM.get()
                .create(serverLevel);
        if (phantom == null) {
            delivered = true;
            return;
        }

        phantom.setTransportTarget(target);
        phantom.setRiderGoal(this);
        phantom.moveTo(rider.getX(), rider.getY() + 0.5, rider.getZ(), rider.getYRot(), 0.0F);
        serverLevel.addFreshEntity(phantom);

        rider.getNavigation().stop();
        rider.startRiding(phantom, true);
    }

    @Override
    public void tick() {
        if (delivered || phantom == null) return;
        LivingEntity sensed = rider.getTarget();
        if (sensed != null && sensed.isAlive()) {
            if (returning) {
                cancelReturn(phantom);
            }
            target = sensed;
            phantom.setTransportTarget(sensed);
        } else if (!returning) {
            // El pasajero perdio su objetivo: volvemos al punto de recogida.
            beginReturn(phantom);
            return;
        }
        // Tambien se llama desde CustomPhantom.tick(); es idempotente.
        checkDelivery(phantom);
    }

    /** Llamado desde el phantom cada tick, asi no dependemos de este goal. */
    public void checkDelivery(CustomPhantom carrier) {
        if (delivered || carrier == null || !carrier.isAlive()) return;
        if (this.phantom != null && this.phantom != carrier) return;

        // Si el pasajero muere, el phantom muere con el.
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
            target = sensed;
            carrier.setTransportTarget(sensed);
        } else if (!returning) {
            beginReturn(carrier);
            return;
        }

        // Unico punto de entrega: el ultimo nodo del path que encontro el
        // pathfinder. No se comprueba ni se busca una alternativa aqui.
        BlockPos landing = carrier.getLandingPos();
        if (landing != null) {
            deliverAt(carrier, landing);
        }
    }

    /** Objetivo perdido: el phantom vuela de vuelta al punto original con el jinete. */
    private void beginReturn(CustomPhantom carrier) {
        if (returning) return;
        returning = true;
        target = null;
        carrier.setTransportTarget(null);
        carrier.setReturnPos(origin != null ? origin : rider.getOnPos().above());
    }

    /** Target nuevo: se cancela el regreso, pero se conserva la posicion original. */
    private void cancelReturn(CustomPhantom carrier) {
        returning = false;
        carrier.setReturnPos(null);
    }

    /** Suelta al pasajero y elimina el phantom. */
    private void deliverAt(CustomPhantom carrier, BlockPos stand) {
        float yRot = returning && origin != null ? originYRot : rider.getYRot();
        float xRot = returning && origin != null ? originXRot : rider.getXRot();
        rider.stopRiding();
        // Soltar donde esta el phantom (ya ha llegado fisicamente): nada de tp.
        rider.moveTo(carrier.getX(), carrier.getY(), carrier.getZ(), yRot, xRot);
        rider.getNavigation().stop();
        rider.fallDistance = 0.0F;

        carrier.setRiderGoal(null);
        carrier.discard();
        this.phantom = null;
        delivered = true;
        returning = false;
        target = null;
        origin = null;
        nextUseTick = rider.level().getGameTime() + COOLDOWN_TICKS;
    }

    @Override
    public void stop() {
        if (!delivered && phantom != null) {
            if (rider.getVehicle() == phantom) {
                rider.stopRiding();
            }
            phantom.setRiderGoal(null);
            phantom.discard();
            phantom = null;
        }
        target = null;
        origin = null;
        returning = false;
    }
}
