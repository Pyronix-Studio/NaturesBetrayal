package es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.mobs;

import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class PhantomRiderGoal extends Goal {

    private final Mob zombie;
    private CustomPhatom phantom;
    private LivingEntity target;
    private BlockPos landingPos;

    private static final double TRIGGER_Y_DIFF  = 20.0;
    private static final double LANDING_DIST_SQ = 2.0 * 2.0;

    public PhantomRiderGoal(Mob zombie) {
        this.zombie = zombie;
    }

    @Override
    public boolean canUse() {
        if (!zombie.getNavigation().isDone()) return false;
        target = zombie.getTarget();
        if (target == null) return false;
        return Math.abs(target.getY() - zombie.getY()) > TRIGGER_Y_DIFF;
    }

    @Override
    public boolean canContinueToUse() {
        if (target == null || !target.isAlive()) return false;
        if (phantom == null || !phantom.isAlive()) return false;
        // Parar cuando el phantom llegue al landing
        if (landingPos != null && hasArrivedAtLanding()) return false;
        return true;
    }

    private boolean hasArrivedAtLanding() {
        double dx = landingPos.getX() + 0.5 - phantom.getX();
        double dy = landingPos.getY()       - phantom.getY();
        double dz = landingPos.getZ() + 0.5 - phantom.getZ();
        return dx*dx + dy*dy + dz*dz < LANDING_DIST_SQ;
    }

    @Override
    public void start() {
        phantom = NaturesBetrayal.registries.ENTITY_TYPES.CUSTOM_PHANTOM.get().create(zombie.level());
        if (phantom == null) return;

        phantom.moveTo(zombie.getX(), zombie.getY() + 1, zombie.getZ(), 0, 0);
        phantom.setTarget(target);

        ((ServerLevel) zombie.level()).addFreshEntity(phantom);
        phantom.getNavigation().stop();
        zombie.startRiding(phantom, true);
    }

    @Override
    public void tick() {
        if (phantom == null) return;

        // Actualizar target
        LivingEntity currentTarget = zombie.getTarget();
        if (currentTarget != null) target = currentTarget;
        phantom.setTarget(target);

        // Calcular landing una vez que estemos cerca del target en XZ
        if (landingPos == null && isCloseEnoughToCalculateLanding()) {
            landingPos = phantom.findLandingPos();
            // Cambiar el target del phantom al landing pos
            phantom.setLandingMode(landingPos);
        }
    }

    // Cuando estamos a menos de 8 bloques en XZ del target calculamos el landing
    private boolean isCloseEnoughToCalculateLanding() {
        if (target == null) return false;
        double dx = target.getX() - phantom.getX();
        double dz = target.getZ() - phantom.getZ();
        return dx*dx + dz*dz < 8.0 * 8.0;
    }

    @Override
    public void stop() {
        if (phantom != null) {
            zombie.stopRiding();
            // El zombie cae solo desde donde está el phantom — sin tp
            phantom.discard();
            phantom = null;
        }
        landingPos = null;
        target = null;
    }
}