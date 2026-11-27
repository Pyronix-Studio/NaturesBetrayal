package es.pyronixstudio.natures_betrayal.common.mixins.mobs.injects;

import es.pyronixstudio.natures_betrayal.common.config.NaturesBetrayalConfig;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractFish.class)
public abstract class FishMixin {

    @Unique
    private static final float VANILLA_WATER_ACCELERATION = 0.01F;

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/AbstractFish;moveRelative(FLnet/minecraft/world/phys/Vec3;)V"))
    private void naturesBetrayal$moveRelative(AbstractFish instance, float amount, Vec3 relative) {
        float speed = (float) (VANILLA_WATER_ACCELERATION * NaturesBetrayalConfig.WATER_MOVEMENT_SPEED_MODIFIER.get());
        instance.moveRelative(speed, relative);
    }
}