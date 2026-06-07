package es.pyronixstudio.natures_betrayal.common.mixins.mobs;

import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.HostibilityInjectorManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobGoalMixin {
    @Shadow
    protected GoalSelector targetSelector;

    @Shadow
    protected GoalSelector goalSelector;

    private static HostibilityInjectorManager hostibilityInjectorManager;

    private static HostibilityInjectorManager getManager() {
        if (hostibilityInjectorManager == null) {
            hostibilityInjectorManager = new HostibilityInjectorManager();
        }
        return hostibilityInjectorManager;
    }
    @Inject(method = "<init>", at = @At("TAIL"))
    private void naturesBetrayal$registerGoals(EntityType<?> entityType, Level level, CallbackInfo callbackInfo) {
        Mob self = (Mob) (Object) this;
        getManager().inject(self, goalSelector, targetSelector, entityType, level);

    }


}
