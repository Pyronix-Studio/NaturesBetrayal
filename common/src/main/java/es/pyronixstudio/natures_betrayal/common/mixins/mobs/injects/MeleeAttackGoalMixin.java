package es.pyronixstudio.natures_betrayal.common.mixins.mobs.injects;

import es.pyronixstudio.natures_betrayal.common.config.NaturesBetrayalConfig;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MeleeAttackGoal.class)
public abstract class MeleeAttackGoalMixin {

    @Shadow
    @Final
    @Mutable
    private boolean followingTargetEvenIfNotSeen;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen, CallbackInfo ci) {
        boolean xrayEnable = NaturesBetrayalConfig.XRAY_TARGET.get();
        if(xrayEnable){
            this.followingTargetEvenIfNotSeen = true;
        }
    }

}
