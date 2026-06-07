package es.pyronixstudio.natures_betrayal.common.mixins.mobs;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MobAttributeMixin {

    @Inject(method = "createMobAttributes", at = @At("RETURN"))
    private static void naturesBetrayal$createMobAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir){
        cir.getReturnValue().add(Attributes.ATTACK_DAMAGE).add(Attributes.FOLLOW_RANGE);
    }

}
