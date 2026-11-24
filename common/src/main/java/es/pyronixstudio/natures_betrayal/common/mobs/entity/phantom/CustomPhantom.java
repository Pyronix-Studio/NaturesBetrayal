package es.pyronixstudio.natures_betrayal.common.mobs.entity.phantom;

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
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Phantom de transporte. Nunca se teletransporta: pide un path real al
 * FlyNodeEvaluator vanilla y suelta al jinete solo cuando ha llegado.
 */
public class CustomPhantom extends FlyingMob {

    @Nullable
    private LivingEntity transportTarget;
    @Nullable
    private BlockPos landingPos;
    @Nullable
    private BlockPos returnPos;
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
        this.transportTarget = target;
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

    /** Orden de regreso: vuela hasta este punto y entrega alli. */
    public void setReturnPos(@Nullable BlockPos pos) {
        this.returnPos = pos;
        this.landingPos = null;
        if (deliverGoal != null) {
            deliverGoal.forceRepath = true;
        }
    }

    /** True cuando el phantom paró bajo un techo en vez de subir al tejado. */
    public boolean isCeilingBlocked() {
        return deliverGoal != null && deliverGoal.ceilingBlocked;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;

        // Huérfano: el goal se cortó o el jinete murió. Un phantom sin nadie
        // encima no debe quedarse volando por el mundo.
        if (riderGoal != null && getFirstPassenger() == null && this.tickCount > 5) {
            riderGoal = null;
            kill();
            return;
        }
        if (riderGoal != null) {
            riderGoal.checkDelivery(this);
        }
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

    // -------------------------------------------------------

    class DeliverPassengerGoal extends Goal {

        private static final int REPATH_COOLDOWN = 20;

        @Nullable
        private BlockPos requestedGoal;
        @Nullable
        private BlockPos activeGoal;
        private int repathTimer;
        private boolean forceRepath;
        /** Hay un techo entre el jinete y el objetivo: tope de vuelo, sin soltar. */
        private boolean ceilingBlocked;
        /** Bloque exacto del techo; cuando desaparezca reanudamos el ascenso. */
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

            // Regresando al punto de recogida: el tope de techo ya no aplica.
            if (ceilingBlocked && returnPos != null) {
                ceilingBlocked = false;
                ceilingPos = null;
                forceRepath = true;
            }

            // Tope de vuelo: seguimos montados, sin entregar, hasta que el
            // mob rompa el techo. Entonces reanudamos la ruta hacia el objetivo.
            if (ceilingBlocked) {
                if (ceilingPos != null && !level().getBlockState(ceilingPos).isSolid()) {
                    ceilingBlocked = false;
                    ceilingPos = null;
                    forceRepath = true;
                }
                // No llamar a stopFlying(): frenaba el phantom cada tick y
                // borraba el path, dejandolo avanzar 1mm cada repath.
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
                // No hace falta clavarse en el nodo exacto: vale cualquier
                // bloque seguro a 1-2 bloques del objetivo.
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
                // El path desaparecio (timeout/stuck): lo rehacemos ya mismo.
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

        /** Objetivo exacto alcanzable, o null. Nunca un camino truncado. */
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

            // Cubo cerrado: si hay un techo entre nosotros y el objetivo, no
            // intentamos subir a el. Paramos justo debajo para que el mob pueda
            // romperlo con el goal de romper, como si estuviera a 1 bloque.
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

        /** Nunca aceptamos un camino que termine por debajo del jugador. */
        private boolean endsAtOrAbove(Path path, int minY) {
            if (path == null || !path.canReach()) {
                return false;
            }
            Node end = path.getEndNode();
            return end != null && end.y >= minY;
        }

        /**
         * Igual pero sin exigir el bloque exacto ni que el pathfinder marque
         * "reached": basta con que el último nodo quede cerca y a su altura.
         * Esto evita repaths caros contra una coordenada concreta.
         */
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
