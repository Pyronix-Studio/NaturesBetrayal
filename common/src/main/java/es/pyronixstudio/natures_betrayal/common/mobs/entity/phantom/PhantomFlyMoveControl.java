package es.pyronixstudio.natures_betrayal.common.mobs.entity.phantom;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

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


    public void stop() {
        this.operation = Operation.WAIT;
    }

    @Override
    public void tick() {
        if (this.operation != Operation.MOVE_TO) {

            this.mob.setDeltaMovement(this.mob.getDeltaMovement().scale(0.5D));
            faceMovement(MAX_YAW_STEP * 0.6F, MAX_PITCH_STEP * 0.6F);
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

        faceMovement(MAX_YAW_STEP, MAX_PITCH_STEP);
    }

    private void faceMovement(float yawStep, float pitchStep) {
        Vec3 velocity = this.mob.getDeltaMovement();
        double horizontal = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
        if (velocity.lengthSqr() < 1.0E-4) {
            this.mob.setXRot(rotateTowards(this.mob.getXRot(), 0.0F, pitchStep * 0.5F));
            updateBank(this.mob.getYRot());
            return;
        }


        float targetYaw = (float) (Mth.atan2(velocity.z, velocity.x) * (180.0 / Math.PI)) - 90.0F;
        float newYaw = rotateTowards(this.mob.getYRot(), targetYaw, yawStep);
        this.mob.setYRot(newYaw);
        this.mob.yBodyRot = newYaw;

        float targetPitch = -(float) (Mth.atan2(velocity.y, horizontal) * (180.0 / Math.PI));
        targetPitch = Mth.clamp(targetPitch, -MAX_PITCH, MAX_PITCH);
        this.mob.setXRot(rotateTowards(this.mob.getXRot(), targetPitch, pitchStep));

        updateBank(newYaw);
    }

    private void updateBank(float newYaw) {
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
