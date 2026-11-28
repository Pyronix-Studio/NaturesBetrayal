package es.pyronixstudio.natures_betrayal.common.mixins.mobs.injects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSkeleton.class)
public abstract class SkeletonAimBotMixin {

    @Unique
    private static final double NB_ARROW_DRAG = 0.99D;

    @Unique
    private static final double NB_ARROW_GRAVITY = 0.05D;

    @Unique
    private static final double NB_MAX_FLIGHT_TICKS = 60.0D;

    @Unique
    private static final int NB_SOLVER_ITERATIONS = 6;

    @Unique
    private static final double NB_VELOCITY_SMOOTHING = 0.5D;

    @Unique
    private static final double NB_TELEPORT_THRESHOLD = 8.0D;

    @Unique
    private static final float NB_INACCURACY = 0.0F;

    @Unique
    private LivingEntity naturesBetrayal$trackedTarget;
    @Unique
    private double naturesBetrayal$lastX;
    @Unique
    private double naturesBetrayal$lastZ;
    @Unique
    private double naturesBetrayal$velX;
    @Unique
    private double naturesBetrayal$velZ;

    @Unique
    private AbstractSkeleton naturesBetrayal$self() {
        return (AbstractSkeleton) (Object) this;
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void naturesBetrayal$trackTargetVelocity(CallbackInfo ci) {
        AbstractSkeleton self = naturesBetrayal$self();
        if (self.level().isClientSide) {
            return;
        }

        LivingEntity target = self.getTarget();
        if (target == null) {
            this.naturesBetrayal$trackedTarget = null;
            this.naturesBetrayal$velX = 0.0D;
            this.naturesBetrayal$velZ = 0.0D;
            return;
        }

        if (target != this.naturesBetrayal$trackedTarget) {
            this.naturesBetrayal$trackedTarget = target;
            this.naturesBetrayal$lastX = target.getX();
            this.naturesBetrayal$lastZ = target.getZ();
            this.naturesBetrayal$velX = 0.0D;
            this.naturesBetrayal$velZ = 0.0D;
            return;
        }

        double dx = target.getX() - this.naturesBetrayal$lastX;
        double dz = target.getZ() - this.naturesBetrayal$lastZ;
        this.naturesBetrayal$lastX = target.getX();
        this.naturesBetrayal$lastZ = target.getZ();

        if (dx * dx + dz * dz > NB_TELEPORT_THRESHOLD * NB_TELEPORT_THRESHOLD) {
            this.naturesBetrayal$velX = 0.0D;
            this.naturesBetrayal$velZ = 0.0D;
            return;
        }

        this.naturesBetrayal$velX += (dx - this.naturesBetrayal$velX) * NB_VELOCITY_SMOOTHING;
        this.naturesBetrayal$velZ += (dz - this.naturesBetrayal$velZ) * NB_VELOCITY_SMOOTHING;
    }


    @Redirect(method = "performRangedAttack(Lnet/minecraft/world/entity/LivingEntity;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;shoot(DDDFF)V"))
    private void naturesBetrayal$predictiveShoot(AbstractArrow arrow, double x, double y, double z, float speed, float inaccuracy) {
        LivingEntity target = naturesBetrayal$self().getTarget();
        if (target == null) {
            arrow.shoot(x, y, z, speed, NB_INACCURACY);
            return;
        }

        Vec3 origin = arrow.position();
        double aimX = target.getX();
        double aimY = target.getY(1.0D / 3.0D);
        double aimZ = target.getZ();

        double velX = this.naturesBetrayal$velX;
        double velZ = this.naturesBetrayal$velZ;


        double ticks = origin.distanceTo(new Vec3(aimX, aimY, aimZ)) / speed;

        double launchX = x;
        double launchY = y;
        double launchZ = z;

        for (int i = 0; i < NB_SOLVER_ITERATIONS; i++) {
            ticks = Math.clamp(ticks, 1.0D, NB_MAX_FLIGHT_TICKS);

            double futureX = aimX + velX * ticks;
            double futureZ = aimZ + velZ * ticks;


            double a = (1.0D - Math.pow(NB_ARROW_DRAG, ticks)) / (1.0D - NB_ARROW_DRAG);

            double gravityDrop = NB_ARROW_GRAVITY / (1.0D - NB_ARROW_DRAG) * (ticks - a);

            launchX = (futureX - origin.x) / a;
            launchY = (aimY - origin.y + gravityDrop) / a;
            launchZ = (futureZ - origin.z) / a;

            double required = Math.sqrt(launchX * launchX + launchY * launchY + launchZ * launchZ);
            ticks = ticks * required / speed;
        }

        arrow.shoot(launchX, launchY, launchZ, speed, NB_INACCURACY);
    }


}
