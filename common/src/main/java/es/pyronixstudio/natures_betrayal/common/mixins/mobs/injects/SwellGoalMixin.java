package es.pyronixstudio.natures_betrayal.common.mixins.mobs.injects;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.pathfinder.Path;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SwellGoal.class)
public abstract class SwellGoalMixin extends Goal {

    @Final
    @Shadow
    private Creeper creeper;

    @Shadow
    private LivingEntity target;

    @Inject(method = "start", at = @At("HEAD"), cancellable = true)
    private void start(CallbackInfo ci) {
        ci.cancel();
        this.target = this.creeper.getTarget();
    }

    @Inject(method = "stop", at = @At("HEAD"))
    private void stop(CallbackInfo ci) {
        this.creeper.setSwellDir(-1);
    }

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void canUse(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);

        PathNavigation navigation = this.creeper.getNavigation();
        LivingEntity target = this.creeper.getTarget();

        if (target == null || !target.isAlive()) return;

        if (this.creeper.hasLineOfSight(target)) {
            if (this.creeper.distanceToSqr(target) < 9.0D) {
                cir.setReturnValue(true);
            }
            return;
        }

        Path path = navigation.createPath(target, 0);

        if (path == null || !navigation.isDone()) return;

        BlockPos end = path.getEndNode().asBlockPos();

        double distance = this.creeper.distanceToSqr(
                end.getX() + 0.5,
                end.getY(),
                end.getZ() + 0.5
        );

        double targetDistance = this.creeper.distanceToSqr(target);

        if (distance > targetDistance) return;

        cir.setReturnValue(true);
    }


    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.creeper.getTarget();

        if (target == null || !target.isAlive()) return false;

        if (this.creeper.hasLineOfSight(target) && this.creeper.distanceToSqr(target) >= 9.0D) {
            return false;
        }

        return true;
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(CallbackInfo ci) {
        ci.cancel();

        LivingEntity target = this.creeper.getTarget();

        if (target == null || !target.isAlive()) {
            this.creeper.setSwellDir(-1);
            return;
        }

        double distance = this.creeper.distanceToSqr(target);

        if (distance < 9.0D && this.creeper.hasLineOfSight(target)) {
            this.creeper.getNavigation().stop();
            this.creeper.setSwellDir(1);
        } else if (!this.creeper.hasLineOfSight(target)) {

            Path path = this.creeper.getNavigation().createPath(target, 0);
            if (path == null || path.getEndNode() == null) {
                this.creeper.setSwellDir(-1);
                return;
            }
            BlockPos end = path.getEndNode().asBlockPos();
            double endDistance = this.creeper.distanceToSqr(
                    end.getX() + 0.5,
                    end.getY(),
                    end.getZ() + 0.5
            );
            this.creeper.setSwellDir(endDistance <= distance ? 1 : -1);
        } else if (distance > 49.0D) {
            this.creeper.setSwellDir(-1);
        }
    }
}
