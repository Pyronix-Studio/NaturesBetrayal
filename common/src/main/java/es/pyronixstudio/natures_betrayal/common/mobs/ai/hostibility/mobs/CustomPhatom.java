package es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.mobs;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class CustomPhatom extends FlyingMob {

    public CustomPhatom(EntityType<? extends FlyingMob> type, Level level) {
        super(type, level);
        this.moveControl = new CustomFlyMoveControl(this);
    }
    // En CustomPhatom
    @Nullable
    private BlockPos landingPos;

    public void setLandingMode(BlockPos pos) {
        this.landingPos = pos;
    }

    @Nullable
    public BlockPos getLandingPos() {
        return landingPos;
    }
    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation nav = new FlyingPathNavigation(this, level);
        nav.setCanOpenDoors(false);
        nav.setCanFloat(true);
        nav.setCanPassDoors(true);
        return nav;
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
        this.goalSelector.addGoal(1, new FlyToTargetGoal(this));
    }

    @Override
    public boolean hurt(DamageSource src, float amount) { return false; }

    @Override
    protected SoundEvent getHurtSound(DamageSource src) { return null; }

    @Override
    protected SoundEvent getDeathSound() { return null; }

    // -------------------------------------------------------
    // MoveControl custom: solo rota en YAW, sin pitch
    // -------------------------------------------------------

    static class CustomFlyMoveControl extends MoveControl {

        private final CustomPhatom mob;

        CustomFlyMoveControl(CustomPhatom mob) {
            super(mob);
            this.mob = mob;
        }

        @Override
        public void tick() {
            if (this.operation != Operation.MOVE_TO) return;

            this.operation = Operation.WAIT;

            double dx = this.wantedX - mob.getX();
            double dy = this.wantedY - mob.getY();
            double dz = this.wantedZ - mob.getZ();
            double distSqr = dx * dx + dy * dy + dz * dz;

            if (distSqr < 1.0) return;

            double dist = Math.sqrt(distSqr);

            // velocidad desde atributo
            double speed = mob.getAttributeValue(Attributes.FLYING_SPEED) * this.speedModifier;

            // aplicar movimiento
            mob.setDeltaMovement(
                    mob.getDeltaMovement()
                            .add(
                                    (dx / dist) * speed * 0.1,
                                    (dy / dist) * speed * 0.1,
                                    (dz / dist) * speed * 0.1
                            )
            );

            // limitar velocidad máxima
            var vel = mob.getDeltaMovement();
            double currentSpeed = vel.length();
            if (currentSpeed > speed) {
                mob.setDeltaMovement(vel.scale(speed / currentSpeed));
            }

            // rotar solo en YAW — sin tocar xRot para evitar el giro raro
            float targetYaw = (float)(Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0f;
            mob.setYRot(rotateTowards(mob.getYRot(), targetYaw, 10.0f));
            mob.yBodyRot = mob.getYRot();
        }

        private static float rotateTowards(float current, float target, float maxStep) {
            float diff = Mth.wrapDegrees(target - current);
            if (diff > maxStep)  diff = maxStep;
            if (diff < -maxStep) diff = -maxStep;
            return current + diff;
        }
    }

    public BlockPos findLandingPos() {
        // Primero intentar al nivel del target
        LivingEntity t = getTarget();
        if (t == null) return blockPosition();

        BlockPos targetPos = t.blockPosition();

        // Buscar bloque sólido en un radio de 3 bloques alrededor del player
        for (int r = 0; r <= 3; r++) {
            for (int x = -r; x <= r; x++) {
                for (int z = -r; z <= r; z++) {
                    // Solo el borde del radio actual
                    if (Math.abs(x) != r && Math.abs(z) != r) continue;

                    BlockPos candidate = targetPos.offset(x, 0, z);

                    // Buscar suelo hacia abajo desde la Y del player
                    for (int y = 0; y >= -5; y--) {
                        BlockPos ground = candidate.offset(0, y, 0);
                        BlockPos above  = ground.above();
                        BlockPos above2 = above.above();

                        if (level().getBlockState(ground).isSolid()
                                && level().getBlockState(above).isAir()
                                && level().getBlockState(above2).isAir()) {
                            return above; // posición donde aterrizar
                        }
                    }
                }
            }
        }

        // Fallback: misma Y que el player
        return targetPos;
    }

    // -------------------------------------------------------

    static class FlyToTargetGoal extends Goal {

        private static final double ARRIVE_DIST = 1.5;

        private final CustomPhatom phantom;
        private LivingEntity target;

        FlyToTargetGoal(CustomPhatom phantom) {
            this.phantom = phantom;
        }

        @Override
        public boolean canUse() {
            Entity passenger = phantom.getFirstPassenger();
            if (passenger instanceof Mob mob) {
                LivingEntity t = mob.getTarget();
                if (t != null && t.isAlive()) {
                    this.target = t;
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            if (target == null || !target.isAlive()) return false;
            double dx = target.getX() - phantom.getX();
            double dy = target.getY() - phantom.getY();
            double dz = target.getZ() - phantom.getZ();
            return dx*dx + dy*dy + dz*dz > ARRIVE_DIST * ARRIVE_DIST;
        }

        @Override
        public void tick() {
            // Si hay landing pos, ir ahí en vez de al target
            BlockPos landing = phantom.getLandingPos();
            if (landing != null) {
                phantom.getMoveControl().setWantedPosition(
                        landing.getX() + 0.5,
                        landing.getY(),
                        landing.getZ() + 0.5,
                        1.0
                );
                return;
            }

            if (target == null) return;
            phantom.getMoveControl().setWantedPosition(
                    target.getX(),
                    target.getY() + 1.0,
                    target.getZ(),
                    1.0
            );
        }

        @Override
        public void stop() {
            target = null;
            phantom.setDeltaMovement(phantom.getDeltaMovement().scale(0.0));
        }
    }

    // -------------------------------------------------------

    private double distSqr() {
        return 0;
    }

    static double distSqr(CustomPhatom phantom, LivingEntity target) {
        double dx = target.getX() - phantom.getX();
        double dy = target.getY() - phantom.getY();
        double dz = target.getZ() - phantom.getZ();
        return dx * dx + dy * dy + dz * dz;
    }
}