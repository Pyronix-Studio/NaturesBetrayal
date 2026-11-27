package es.pyronixstudio.natures_betrayal.common.mixins.mobs.injects;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import es.pyronixstudio.natures_betrayal.common.config.NaturesBetrayalConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Ghast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "net.minecraft.world.entity.monster.Ghast$GhastShootFireballGoal")
public abstract class GhastShootFireballGoalMixin {

    @WrapOperation(method = "tick", at =
        @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Ghast;hasLineOfSight(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean naturesBetrayal$hasLineOfSight(Ghast instance, Entity entity, Operation<Boolean> original){
        if(NaturesBetrayalConfig.GHAST_NOT_LINE_SIGHT.get())
            return true;

        return original.call(instance, entity);
    }

    @ModifyConstant(method = "tick", constant = @Constant(doubleValue = 4096.0D))
    private double  naturesBetrayal$shootDistance(double original){
        double distance = NaturesBetrayalConfig.GHAST_SHOOT_RANGE.get();
        return distance * distance;
    }

}
