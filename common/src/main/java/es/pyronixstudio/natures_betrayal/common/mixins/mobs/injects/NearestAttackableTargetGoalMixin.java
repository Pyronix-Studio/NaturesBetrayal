package es.pyronixstudio.natures_betrayal.common.mixins.mobs.injects;

import es.pyronixstudio.natures_betrayal.common.config.NaturesBetrayalConfig;
import es.pyronixstudio.natures_betrayal.common.mixins.mobs.accessor.NearestAttackableTargetGoalAcessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(NearestAttackableTargetGoal.class)
public abstract class NearestAttackableTargetGoalMixin {

    @Inject(method = "<init>(Lnet/minecraft/world/entity/Mob;Ljava/lang/Class;IZZLjava/util/function/Predicate;)V", at = @At("TAIL"))
    private void onInit(Mob mob, Class<?> targetType, int i, boolean bl, boolean bl2, @Nullable Predicate<LivingEntity>  predicate, CallbackInfo ci) {
        boolean xrayEnable = NaturesBetrayalConfig.XRAY_TARGET.get();
        if(!xrayEnable)
            return;
        if(!Player.class.isAssignableFrom(targetType))
            return;
        NearestAttackableTargetGoalAcessor acessor = (NearestAttackableTargetGoalAcessor)this;
        acessor.targetingConditions().ignoreLineOfSight();
    }

}
