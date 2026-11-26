package es.pyronixstudio.natures_betrayal.common.mixins.mobs.injects;

import es.pyronixstudio.natures_betrayal.common.config.NaturesBetrayalConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.entity.monster.Spider$SpiderTargetGoal")
public abstract class SpiderTargetGoalMixin<T extends LivingEntity> extends NearestAttackableTargetGoal<T>{


    public SpiderTargetGoalMixin(Mob mob, Class<T> class_, boolean bl) {
        super(mob, class_, bl);
    }

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void naturesBetrayal$canUse(CallbackInfoReturnable<Boolean> cir){
        if (NaturesBetrayalConfig.SPIDER_ATTACK_ON_LIGHT.get()) {
            cir.setReturnValue(super.canUse());
        }
    }
}
