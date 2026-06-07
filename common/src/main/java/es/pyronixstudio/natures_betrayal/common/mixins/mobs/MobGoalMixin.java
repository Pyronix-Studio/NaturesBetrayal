package es.pyronixstudio.natures_betrayal.common.mixins.mobs;

import es.pyronixstudio.natures_betrayal.common.goals.HurtIgnoreSpecieByTargetGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(Mob.class)
public abstract class MobGoalMixin {
    @Shadow
    protected GoalSelector targetSelector;

    @Shadow
    protected GoalSelector goalSelector;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void naturesBetrayal$registerGoals(EntityType<?> entityType, Level level, CallbackInfo callbackInfo){
        Mob self = (Mob) (Object) this;
        if(self instanceof Animal) {

            if (self instanceof PathfinderMob pathfinderMob) {
                borrarGoal(goalSelector, PanicGoal.class);
                desplazarGoals(1, targetSelector);
                desplazarGoals(1, goalSelector);

                self.getAttributes().getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(2.0D);
                self.getAttributes().getInstance(Attributes.FOLLOW_RANGE).setBaseValue(200);

                goalSelector.addGoal(0, new MeleeAttackGoal(pathfinderMob, 1.2D, true));
                HurtByTargetGoal hurtByTargetGoal = new HurtIgnoreSpecieByTargetGoal(pathfinderMob, new Class[0]);
                hurtByTargetGoal.setAlertOthers();
                targetSelector.addGoal(0, hurtByTargetGoal);


                for (WrappedGoal goal : goalSelector.getAvailableGoals()) {
                    System.out.println("GOAL CLASS: " + goal.getGoal().getClass());
                }

            }
        }
    }

    private static void borrarGoal(GoalSelector goalSelector, Class<? extends Goal> goalClazz){
        List<WrappedGoal> existing = new ArrayList<>(goalSelector.getAvailableGoals());
        for(WrappedGoal wrappedGoal : existing){
            Goal goal = wrappedGoal.getGoal();
            if(goalClazz.isInstance(goal))
                goalSelector.removeGoal(wrappedGoal.getGoal());
        }
    }

    private static void desplazarGoals(int cantidad, GoalSelector goalSelector){
        List<WrappedGoal> existing = new ArrayList<>(goalSelector.getAvailableGoals());

        for(WrappedGoal wrappedGoal : existing){
            goalSelector.removeGoal(wrappedGoal.getGoal());
        }

       for(WrappedGoal wrappedGoal : existing){
           goalSelector.addGoal(wrappedGoal.getPriority() + cantidad, wrappedGoal.getGoal());
       }
    }


}
