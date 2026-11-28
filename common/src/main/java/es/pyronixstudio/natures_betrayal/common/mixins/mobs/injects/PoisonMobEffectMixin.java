package es.pyronixstudio.natures_betrayal.common.mixins.mobs.injects;

import es.pyronixstudio.natures_betrayal.common.config.NaturesBetrayalConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "net.minecraft.world.effect.PoisonMobEffect")
public abstract class PoisonMobEffectMixin {


  @ModifyConstant(method = "applyEffectTick", constant = @Constant(floatValue = 1.0F, ordinal = 0))
    private float naturesbetrayal$healthCheck(float original){
      boolean canKill = NaturesBetrayalConfig.POISON_CAN_KILL.get();
      return canKill ? 0.0F : original;
  }

}
