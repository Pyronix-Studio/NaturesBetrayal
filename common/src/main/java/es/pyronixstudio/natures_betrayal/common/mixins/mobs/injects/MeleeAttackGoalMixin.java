package es.pyronixstudio.natures_betrayal.common.mixins.mobs.injects;

import es.pyronixstudio.natures_betrayal.common.config.NaturesBetrayalConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MeleeAttackGoal.class)
public abstract class MeleeAttackGoalMixin {

    @Shadow
    @Final
    @Mutable
    private boolean followingTargetEvenIfNotSeen;

    @Shadow
    @Final
    protected PathfinderMob mob;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void naturesBetrayal$onInit(PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen, CallbackInfo ci) {
        boolean xrayEnable = NaturesBetrayalConfig.XRAY_TARGET.get();
        if (xrayEnable) {
            this.followingTargetEvenIfNotSeen = true;
        }
    }

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void naturesBetrayal$canUse(CallbackInfoReturnable<Boolean> cir) {
        naturesBetrayal$canHurt(cir);
    }

    @Inject(method = "canContinueToUse", at = @At("HEAD"), cancellable = true)
    private void naturesBetrayal$canContinueToUse(CallbackInfoReturnable<Boolean> cir) {
        naturesBetrayal$canHurt(cir);
    }

    @Unique
    private void naturesBetrayal$canHurt(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity target = mob.getTarget();
        if (target == null) return;

        if (mob.hasPassenger(target)) {
            cir.setReturnValue(false);
        }
    }
}
