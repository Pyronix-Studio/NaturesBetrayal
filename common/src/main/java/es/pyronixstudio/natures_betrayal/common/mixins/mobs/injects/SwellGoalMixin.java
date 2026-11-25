package es.pyronixstudio.natures_betrayal.common.mixins.mobs.injects;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SwellGoal.class)
public abstract class SwellGoalMixin {

    @Final
    @Shadow
    private Creeper creeper;

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void noLineOfSight(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity target = this.creeper.getTarget();
        if (target != null && !this.creeper.hasLineOfSight(target)) {
            cir.setReturnValue(false);
        }
    }


    @Inject(method = "stop", at = @At("HEAD"))
    private void stop(CallbackInfo ci){
        creeper.setSwellDir(-1);
    }

}
