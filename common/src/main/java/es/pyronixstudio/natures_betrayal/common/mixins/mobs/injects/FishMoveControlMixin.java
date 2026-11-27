package es.pyronixstudio.natures_betrayal.common.mixins.mobs.injects;

import es.pyronixstudio.natures_betrayal.common.config.NaturesBetrayalConfig;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.world.entity.animal.AbstractFish$FishMoveControl")
public abstract class FishMoveControlMixin {

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;",
                    ordinal = 1
            )
    )
    private Vec3 naturesBetrayal$verticalMovement(Vec3 instance, double x, double y, double z) {
        return instance.add(x, y * NaturesBetrayalConfig.WATER_MOVEMENT_SPEED_MODIFIER.get(), z);
    }
}
