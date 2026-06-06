package es.pyronixstudio.natures_betrayal.common.mixins.mobs;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.horse.Horse;import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;
import java.util.List;

@Mixin(Mob.class)
public abstract class MobGoalMixin {
    @Shadow
    protected GoalSelector targetSelector;

    @Shadow
    protected GoalSelector goalSelector;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void naturesBetrayal$registerGoals(EntityType<?> entityType, Level level, CallbackInfo callbackInfo){
        if(level.isClientSide()) return;
        Mob self = (Mob) (Object) this;
        desplazarGoals(1, targetSelector);
        targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(self, Player.class, true));
        if (self instanceof PathfinderMob pathfinderMob) {
            self.getAttributes().getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(2.0D);
            desplazarGoals(1, goalSelector);
            goalSelector.addGoal(0, new MeleeAttackGoal(pathfinderMob, 1.2D, true));
        }

    }


    private static void desplazarGoals(int cantidad, GoalSelector goalSelector){
        List<WrappedGoal> existing = goalSelector.getAvailableGoals().stream()
                .sorted(Comparator.comparingInt(WrappedGoal::getPriority))
                .toList();

        existing.forEach(wrapped -> {
            goalSelector.removeGoal(wrapped.getGoal());
            goalSelector.addGoal(wrapped.getPriority() + cantidad, wrapped.getGoal());
        });
    }


}
