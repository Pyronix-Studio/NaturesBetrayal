package es.pyronixstudio.natures_betrayal.common.mixins.mobs.injects;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TargetingConditions.class)
public abstract class TargetingConditionsMixin {

    @Inject(method = "test", at = @At("HEAD"), cancellable = true)
    private void naturesBetrayal$test(LivingEntity attacker, LivingEntity target, CallbackInfoReturnable<Boolean> cir){
        if(attacker == null)return;

        if (attacker.hasPassenger(target)) {
            cir.setReturnValue(false);
            return;
        }
        if(attacker instanceof OwnableEntity ownableEntity){
            LivingEntity owner = ownableEntity.getOwner();
            if(target.equals(owner))
                cir.setReturnValue(false);
        }
    }
}
