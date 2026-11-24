package es.pyronixstudio.natures_betrayal.common.mobs.entity.phantom;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

/**
 * MoveControl del phantom: mueve por velocidad y orienta el cuerpo hacia donde
 * vuela de verdad (yaw + pitch suaves y un balanceo al girar).
 *
 * No usa FlyingMoveControl directamente porque sus saltos de XRot entre
 * waypoints 3D hacian que el modelo diera vueltas. Aqui todo va limitado por
 * tick y derivado del vector de vuelo.
 */
public class PhantomFlyMoveControl extends MoveControl {

    private static final float MAX_YAW_STEP = 14.0F;
    private static final float MAX_PITCH_STEP = 7.0F;
    private static final float MAX_PITCH = 60.0F;
    private static final float MAX_BANK = 20.0F;
    private static final double ACCELERATION = 0.15;

    private final Mob mob;
    private float lastYaw;

    public PhantomFlyMoveControl(Mob mob) {
        super(mob);
        this.mob = mob;
        this.lastYaw = mob.getYRot();
    }

    /** Corta cualquier waypoint pendiente y deja que el tick frene al phantom. */
    public void stop() {
        this.operation = Operation.WAIT;
    }

    @Override
    public void tick() {
        if (this.operation != Operation.MOVE_TO) {
            if (this.mob instanceof CustomPhantom phantom) {
                phantom.setBank(Mth.lerp(0.08F, phantom.getBank(), 0.0F));
            }
            // Sin waypoint no debe seguir derivando: eso era el "circulo" bajo
            // el suelo. Frenamos suavemente hasta parar.
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().scale(0.5D));
            this.mob.setXRot(rotateTowards(this.mob.getXRot(), 0.0F, MAX_PITCH_STEP * 0.5F));
            return;
        }
        this.operation = Operation.WAIT;

        double dx = this.wantedX - this.mob.getX();
        double dy = this.wantedY - this.mob.getY();
        double dz = this.wantedZ - this.mob.getZ();
        double distSq = dx * dx + dy * dy + dz * dz;
        if (distSq < 1.0E-5) {
            return;
        }

        double dist = Math.sqrt(distSq);
        double speed = this.mob.getAttributeValue(Attributes.FLYING_SPEED) * this.speedModifier;

        Vec3 desired = new Vec3(dx / dist * speed, dy / dist * speed, dz / dist * speed);
        Vec3 velocity = this.mob.getDeltaMovement().lerp(desired, ACCELERATION);
        double currentSpeed = velocity.length();
        if (currentSpeed > speed) {
            velocity = velocity.scale(speed / currentSpeed);
        }
        this.mob.setDeltaMovement(velocity);

        // Mismo convenio que FlyingMoveControl de vanilla: yaw = atan2(dx, dz).
        float targetYaw = (float) (Mth.atan2(dx, dz) * (180.0 / Math.PI));
        float newYaw = rotateTowards(this.mob.getYRot(), targetYaw, MAX_YAW_STEP);
        this.mob.setYRot(newYaw);
        this.mob.yBodyRot = newYaw;

        // Negativo = morro arriba, igual que el phantom vanilla.
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        float targetPitch = -(float) (Mth.atan2(dy, horizontal) * (180.0 / Math.PI));
        targetPitch = Mth.clamp(targetPitch, -MAX_PITCH, MAX_PITCH);
        this.mob.setXRot(rotateTowards(this.mob.getXRot(), targetPitch, MAX_PITCH_STEP));

        // Balanceo segun el giro real, para que el cuerpo acompañe el vuelo.
        float yawChange = Mth.wrapDegrees(newYaw - this.lastYaw);
        this.lastYaw = newYaw;
        if (this.mob instanceof CustomPhantom phantom) {
            float targetBank = Mth.clamp(yawChange * 0.45F, -MAX_BANK, MAX_BANK);
            phantom.setBank(Mth.lerp(0.15F, phantom.getBank(), targetBank));
        }
    }

    private static float rotateTowards(float current, float target, float maxStep) {
        float diff = Mth.wrapDegrees(target - current);
        if (diff > maxStep) {
            diff = maxStep;
        } else if (diff < -maxStep) {
            diff = -maxStep;
        }
        return current + diff;
    }
}
